from locust import HttpUser, task, between
import uuid
import random

class FilmPlatformUser(HttpUser):
    # Kullanıcıların her istek arasında bekleyeceği süre (1 ile 3 saniye arası)
    wait_time = between(1, 3)
    
    def on_start(self):
        """Her sanal kullanıcı teste başlarken bu fonksiyon çalışır. Otomatik üye olup Token alır."""
        self.username = f"user_{uuid.uuid4().hex[:8]}"
        self.password = "test1234"
        self.token = ""
        self.headers = {}
        self.film_ids = []

        # 1. Kayıt Ol (Register)
        self.client.post("/auth/register", json={
            "username": self.username,
            "email": f"{self.username}@mail.com",
            "password": self.password
        })

        # 2. Giriş Yap (Login) ve Token Al
        response = self.client.post("/auth/login", json={
            "username": self.username,
            "password": self.password
        })
        
        if response.status_code == 200:
            # Login olduktan sonra gelen Token'ı JSON icinden alip Header olarak kaydet
            auth_data = response.json()
            self.token = auth_data.get("token")
            if self.token:
                self.headers = {"Authorization": f"Bearer {self.token}"}

    @task(3) # Bu görevi diğerlerine göre 3 kat daha sık yap (Ağır Yük: Okuma)
    def test_list_films(self):
        if self.token:
            self.client.get("/api/films", headers=self.headers, name="List Films")

    @task(1) # Daha az sıklıkla Film Ekle (Yazma yükü)
    def test_add_film(self):
        if self.token:
            response = self.client.post("/api/films", json={
                "filmAdi": f"Test Film {random.randint(1,1000)}",
                "tur": "Aksiyon",
                "yil": 2024,
                "yonetmen": "Yapay Zeka",
                "puan": round(random.uniform(5.0, 9.9), 1)
            }, headers=self.headers, name="Add Film")
            
            # Eklenen filmin IDsini kaydet ki sonra yorum yapabilelim
            if response.status_code == 200 and "id" in response.json():
                self.film_ids.append(response.json()["id"])

    @task(2) # Filme yorum yap ve detaylarını getir
    def test_add_review_and_get_details(self):
        if self.token and len(self.film_ids) > 0:
            # Rastgele bir film seç
            film_id = random.choice(self.film_ids)
            
            # Yoruma ve puana API isteği at
            self.client.post("/api/reviews", json={
                "filmId": film_id,
                "kullaniciAdi": self.username,
                "yorumMetni": "Bu test filmi gercekten basarili!",
                "puan": random.randint(5, 10)
            }, headers=self.headers, name="Add Review")
            
            # Filmin detaylarını (Yorumlarla Birlikte) Getir
            self.client.get(f"/api/films/{film_id}/details", headers=self.headers, name="Get Film Details")