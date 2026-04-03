# Film ve Dizi Öneri Platformu — Proje Raporu

**Proje Adı:** Film ve Dizi Öneri Platformu   
**Ekip Üyeleri:** 
Furkan Demirci - 231307061
Yekta Cengiz - 231307080
**Tarih:** 3 Nisan 2026  
**Ders:** Yazılım Laboratuvarı II 

---

## 1. Giriş

### Problemin Tanımı
Dijital içerik platformlarında film ve dizi sayısının hızla artması, kullanıcıların ilgi alanlarına uygun içerik bulmasını zorlaştırmaktadır. Geleneksel monolitik mimariler artan kullanıcı talebi ve veri hacmi karşısında ölçeklenebilirlik, bağımsız dağıtım ve bakım güçlükleri yaşamaktadır.

### Amaç
Bu projenin amacı; kullanıcıların film/dizi araması yapabildiği, yorum ve puanlama bırakabildiği, JWT tabanlı kimlik doğrulama ile güvenli bir şekilde erişim sağlayabildiği, **mikroservis mimarisi** üzerine kurulu ölçeklenebilir bir platform geliştirmektir. Proje; Docker ile konteynerleştirme, her servise ayrı NoSQL veritabanı, merkezi API Gateway, Prometheus/Grafana ile izleme ve Locust ile yük testi bileşenlerini içermektedir.

---

## 2. Sistem Tasarımı ve Mikroservisler

### 2.1 RESTful Servisler

REST (Representational State Transfer), istemci-sunucu arasındaki iletişimi durumsuz (stateless) HTTP istekleri üzerinden gerçekleştiren bir mimari stildir. Her kaynak benzersiz bir URI ile tanımlanır ve standart HTTP metodları (GET, POST, PUT, DELETE) kullanılarak işlem yapılır. Bu projede tüm servisler RESTful prensiplere uygun şekilde tasarlanmıştır:

| Servis | Metod | Endpoint | Açıklama |
|---|---|---|---|
| Auth Service | POST | `/auth/register` | Yeni kullanıcı kaydı |
| Auth Service | POST | `/auth/login` | Kullanıcı girişi, JWT token üretimi |
| Content Service | GET | `/api/films` | Tüm filmleri listeleme |
| Content Service | GET | `/api/films/{id}` | Tek film detayı |
| Content Service | GET | `/api/films/{id}/details` | Film + yorumları birlikte getirme |
| Content Service | POST | `/api/films` | Yeni film ekleme |
| Content Service | DELETE | `/api/films/{id}` | Film silme |
| Review Service | GET | `/api/reviews` | Tüm yorumları listeleme |
| Review Service | POST | `/api/reviews` | Yeni yorum ekleme |
| Review Service | GET | `/api/reviews/film/{filmId}` | Bir filme ait yorumları getirme |

### 2.2 Richardson Olgunluk Modeli (Richardson Maturity Model)

Richardson Olgunluk Modeli, bir API'nin RESTful olgunluk seviyesini dört kademede değerlendirir:

| Seviye | Tanım | Projede Uygulama |
|---|---|---|
| **Seviye 0 – The Swamp of POX** | Tek bir URI, tek bir HTTP metodu (genellikle POST). | Projede bu seviye aşılmıştır. |
| **Seviye 1 – Resources** | Her kaynak kendi URI'sine sahiptir. | `/api/films`, `/api/reviews`, `/auth` gibi kaynak bazlı ayrım yapılmıştır. |
| **Seviye 2 – HTTP Verbs** | HTTP metodları anlamlarına uygun kullanılır. | `GET` veri okuma, `POST` veri oluşturma, `DELETE` veri silme için kullanılmaktadır. Yanıtlarda uygun durum kodları (200, 201, 401, 403, 404, 503) döndürülmektedir. |
| **Seviye 3 – HATEOAS** | Yanıtlarda ilgili kaynaklara hypermedia bağlantıları sunulur. | Bu seviye mevcut projede uygulanmamıştır. |

**Sonuç:** Proje **Seviye 2** olgunluğundadır.

### 2.3 Sınıf Yapıları

Her mikroservis katmanlı mimari (Layered Architecture) ile yapılandırılmıştır:

```mermaid
classDiagram
    class AuthController {
        -AuthService authService
        +register(RegisterRequest) ResponseEntity
        +login(LoginRequest) ResponseEntity
    }
    class AuthService {
        -UserRepository userRepository
        -JwtUtil jwtUtil
        -PasswordEncoder passwordEncoder
        +register(RegisterRequest) AuthResponse
        +login(LoginRequest) AuthResponse
    }
    class JwtUtil {
        -String jwtSecret
        -long jwtExpiration
        +generateToken(String, String) String
        +extractUsername(String) String
        +extractRole(String) String
        +isTokenValid(String) boolean
    }
    class User {
        -String id
        -String username
        -String email
        -String password
        -String role
    }
    class UserRepository {
        +findByUsername(String) Optional~User~
        +existsByUsername(String) boolean
        +existsByEmail(String) boolean
    }

    AuthController --> AuthService
    AuthService --> UserRepository
    AuthService --> JwtUtil
    UserRepository ..> User
```

```mermaid
classDiagram
    class FilmController {
        -ContentService contentService
        +getFilms() List~Film~
        +getFilmById(String) Film
        +getFilmWithReviews(String) ResponseEntity
        +addFilm(Film) Film
        +deleteFilm(String) void
    }
    class ContentService {
        -FilmRepository repository
        -ReviewClient reviewClient
        +getAllFilms() List~Film~
        +saveFilm(Film) Film
        +getFilmById(String) Film
        +deleteFilm(String) void
        +getFilmReviews(String) List~ReviewDTO~
    }
    class Film {
        -String id
        -String filmAdi
        -String tur
        -int yil
        -String yonetmen
        -double puan
    }
    class FilmRepository {
        +findByFilmAdi(String) Film
    }
    class ReviewClient {
        +getReviewsByFilmId(String) List~ReviewDTO~
    }

    FilmController --> ContentService
    ContentService --> FilmRepository
    ContentService --> ReviewClient
    FilmRepository ..> Film
```

```mermaid
classDiagram
    class ReviewController {
        -ReviewService reviewService
        +getAllReviews() ResponseEntity
        +addReview(ReviewRequest) ResponseEntity
        +getReviewsByFilm(String) ResponseEntity
    }
    class ReviewService {
        -ReviewRepository repository
        +saveReview(ReviewRequest) ReviewResponse
        +getAllReviews() List~ReviewResponse~
        +getReviewsByFilmId(String) List~ReviewResponse~
    }
    class Review {
        -String id
        -String filmId
        -String kullaniciAdi
        -String yorumMetni
        -double puan
    }
    class ReviewRepository {
        +findByFilmId(String) List~Review~
    }

    ReviewController --> ReviewService
    ReviewService --> ReviewRepository
    ReviewRepository ..> Review
```

### 2.4 Sequence Diyagramları

#### Kullanıcı Kayıt Akışı

```mermaid
sequenceDiagram
    participant C as İstemci
    participant D as Dispatcher
    participant A as Auth Service
    participant DB as MongoDB (auth_db)

    C->>D: POST /auth/register {username, email, password}
    D->>A: Forward POST /auth/register
    A->>DB: existsByUsername(username)
    DB-->>A: false
    A->>DB: existsByEmail(email)
    DB-->>A: false
    A->>A: passwordEncoder.encode(password)
    A->>DB: save(User)
    DB-->>A: User kaydedildi
    A->>A: jwtUtil.generateToken(username, role)
    A-->>D: AuthResponse {token, username, role, message}
    D-->>C: 201 Created + JSON
```

#### Film Listeleme Akışı

```mermaid
sequenceDiagram
    participant C as İstemci
    participant D as Dispatcher
    participant AF as AuthFilter
    participant CS as Content Service
    participant DB as MongoDB (content_db)

    C->>D: GET /api/films [Authorization: Bearer token]
    D->>AF: doFilter()
    AF->>AF: isTokenValid(token)
    AF-->>D: Token geçerli, devam et
    D->>CS: Forward GET /api/films
    CS->>DB: findAll()
    DB-->>CS: List~Film~
    CS-->>D: JSON Film Listesi
    D-->>C: 200 OK + JSON
```

#### Film Detay + Yorumlar Akışı (Servisler Arası İletişim)

```mermaid
sequenceDiagram
    participant C as İstemci
    participant D as Dispatcher
    participant CS as Content Service
    participant RS as Review Service
    participant DB1 as MongoDB (content_db)
    participant DB2 as MongoDB (review_db)

    C->>D: GET /api/films/{id}/details [Bearer token]
    D->>CS: Forward GET /api/films/{id}/details
    CS->>DB1: findById(id)
    DB1-->>CS: Film nesnesi
    CS->>RS: GET /api/reviews/film/{id} (Feign Client)
    RS->>DB2: findByFilmId(id)
    DB2-->>RS: List~Review~
    RS-->>CS: List~ReviewDTO~
    CS-->>D: {film: Film, reviews: List}
    D-->>C: 200 OK + JSON
```

#### Yorum Ekleme Akışı

```mermaid
sequenceDiagram
    participant C as İstemci
    participant D as Dispatcher
    participant RS as Review Service
    participant DB as MongoDB (review_db)

    C->>D: POST /api/reviews {filmId, kullaniciAdi, yorumMetni, puan} [Bearer token]
    D->>RS: Forward POST /api/reviews
    RS->>RS: Puan aralığı kontrolü (0-10)
    RS->>RS: Yorum metni boşluk kontrolü
    RS->>DB: save(Review)
    DB-->>RS: Review kaydedildi
    RS-->>D: ReviewResponse
    D-->>C: 200 OK + JSON
```

### 2.5 Dispatcher (API Gateway) Akış Diyagramı

```mermaid
flowchart TD
    A[İstemciden HTTP İsteği Gelir] --> B{URI /auth ile mi başlıyor?}
    B -- Evet --> C[AuthFilter: Token kontrolü atla]
    B -- Hayır --> D{Authorization header var mı?}
    D -- Hayır --> E[401 Unauthorized döndür]
    D -- Evet --> F{JWT Token geçerli mi?}
    F -- Hayır --> G[403 Forbidden döndür]
    F -- Evet --> H[RoutingService ile hedef servis URL belirle]
    C --> H
    H --> I{Hedef URL bulundu mu?}
    I -- Hayır --> J[404 Not Found döndür]
    I -- Evet --> K[ProxyService ile isteği hedef servise ilet]
    K --> L{Servis yanıt verdi mi?}
    L -- Hayır --> M[503 Service Unavailable döndür]
    L -- Evet --> N[LogService ile istek kaydını MongoDB'ye yaz]
    N --> O[Yanıtı istemciye döndür]
```

### 2.6 Algoritmalar ve Karmaşıklık Analizi

| İşlem | Algoritma | Zaman Karmaşıklığı | Açıklama |
|---|---|---|---|
| Film Listeleme | `findAll()` | O(n) | Tüm kayıtlar taranır (n = film sayısı). |
| Film ID ile Getirme | `findById(id)` | O(log n) | MongoDB `_id` indeksi üzerinden B-Tree araması. |
| Film Adı ile Arama | `findByFilmAdi(name)` | O(log n) | İndeks varsa logaritmik, yoksa O(n). |
| Yorum Ekleme | `save(review)` | O(log n) | İndeksli koleksiyona ekleme. |
| Filme Göre Yorum Getirme | `findByFilmId(filmId)` | O(log n + k) | İndeks araması + k adet sonuç döndürme. |
| Kullanıcı Adı Kontrolü | `existsByUsername(name)` | O(log n) | Unique indeks üzerinden arama. |
| JWT Token Üretimi | HMAC-SHA256 | O(1) | Sabit uzunlukta hash hesaplaması. |
| JWT Token Doğrulama | HMAC-SHA256 + Parse | O(1) | Sabit uzunlukta imza doğrulama. |
| Şifre Hashleme | BCrypt | O(1)* | Sabit sayıda round ile hesaplama (*yapılandırılabilir). |
| Rota Çözümleme | `startsWith()` karşılaştırma | O(m) | m = rota kuralı sayısı (3 sabit kural). |

### 2.7 Literatür İncelemesi

- **Mikroservis Mimarisi:** Martin Fowler ve James Lewis (2014) tarafından tanımlanan, uygulamayı küçük, bağımsız çalışan ve dağıtılabilen servisler bütünü olarak yapılandırma yaklaşımıdır. Her servis tek bir iş sürecine odaklanır ve kendi veri deposuna sahiptir (Database-per-Service pattern).
- **REST (Representational State Transfer):** Roy Fielding'in 2000 yılındaki doktora tezinde tanımladığı, HTTP protokolü üzerinden durumsuz (stateless) iletişim sağlayan mimari stildir.
- **Richardson Maturity Model:** Leonard Richardson tarafından önerilen, bir API'nin REST uyumluluğunu dört seviyede değerlendiren modeldir. Seviye 0'dan Seviye 3'e (HATEOAS) doğru olgunluk artar.
- **JWT (JSON Web Token):** RFC 7519 standardına dayanan, taraflar arasında güvenli bilgi aktarımı için kullanılan kompakt ve kendi kendini doğrulayabilen token yapısıdır.
- **API Gateway Pattern:** Tüm istemci isteklerini tek bir giriş noktasından karşılayarak mikroservislere yönlendiren tasarım kalıbıdır. Bu projede Dispatcher servisi bu görevi üstlenmektedir.

---

## 3. Proje Yapısı ve Modüller

### 3.1 Sistem Mimarisi

```mermaid
graph TD
    Client["İstemci (Postman / Locust)"] --> Dispatcher["Dispatcher (API Gateway)<br/>Port: 8080"]

    subgraph Mikroservisler
        Dispatcher --> AuthService["Auth Service<br/>Port: 8081"]
        Dispatcher --> ContentService["Content Service<br/>Port: 8082"]
        Dispatcher --> ReviewService["Review Service<br/>Port: 8083"]
    end

    ContentService -- "Feign Client" --> ReviewService

    subgraph "Veritabanları (NoSQL - Database per Service)"
        AuthService --> MongoAuth[("MongoDB<br/>auth_db")]
        ContentService --> MongoContent[("MongoDB<br/>content_db")]
        ReviewService --> MongoReview[("MongoDB<br/>review_db")]
        Dispatcher --> MongoDispatcher[("MongoDB<br/>dispatcher_db")]
    end

    subgraph "İzleme Altyapısı"
        Prometheus["Prometheus<br/>Port: 9090"] --> AuthService
        Prometheus --> ContentService
        Prometheus --> ReviewService
        Prometheus --> Dispatcher
        Grafana["Grafana<br/>Port: 3000"] --> Prometheus
    end
```

### 3.2 Dizin Yapısı

```
FilmDiziOneriPlatform/
├── dispatcher/                    # API Gateway servisi
│   └── src/main/java/.../dispatcher/
│       ├── DispatcherController.java   # Tüm istekleri karşılayan ana controller
│       ├── filter/AuthFilter.java      # JWT token doğrulama filtresi
│       ├── routing/RoutingService.java # URI bazlı servis yönlendirme
│       ├── proxy/ProxyService.java     # RestTemplate ile istek iletme
│       ├── logging/LogEntry.java       # İstek log modeli
│       ├── logging/LogService.java     # MongoDB'ye log kaydetme
│       └── exception/GlobalExceptionHandler.java
│
├── auth-service/                  # Kimlik doğrulama servisi
│   └── src/main/java/.../auth/
│       ├── controller/AuthController.java
│       ├── service/AuthService.java
│       ├── security/JwtUtil.java
│       ├── config/SecurityConfig.java
│       ├── model/User.java
│       ├── repository/UserRepository.java
│       └── dto/ (RegisterRequest, LoginRequest, AuthResponse)
│
├── content-service/               # Film içerik yönetimi servisi
│   └── src/main/java/.../contentservice/
│       ├── controller/FilmController.java
│       ├── service/ContentService.java
│       ├── client/ReviewClient.java    # Feign Client (Review Service'e istek)
│       ├── model/Film.java
│       ├── repository/FilmRepository.java
│       └── dto/ (FilmRequest, FilmResponse, ReviewDTO)
│
├── review-service/                # Yorum ve puanlama servisi
│   └── src/main/java/.../reviewservice/
│       ├── controller/ReviewController.java
│       ├── service/ReviewService.java
│       ├── model/Review.java
│       ├── repository/ReviewRepository.java
│       └── dto/ (ReviewRequest, ReviewResponse)
│
├── monitoring/
│   ├── prometheus.yml             # Prometheus scrape yapılandırması
│   └── grafana/                   # Grafana dashboard ve provisioning
│
├── docker-compose.yml             # Tüm servislerin orkestrasyon dosyası
└── locustfile.py                  # Yük testi senaryoları
```

### 3.3 Modül İşlevleri

| Modül | Teknoloji | İşlev |
|---|---|---|
| **Dispatcher** | Spring Boot, RestTemplate | Tüm istekleri karşılar, JWT doğrulama yapar, URI'ye göre hedef servise yönlendirir, istek loglarını MongoDB'ye kaydeder. |
| **Auth Service** | Spring Boot, Spring Security, JWT | Kullanıcı kayıt (`register`) ve giriş (`login`) işlemlerini yönetir. Şifreleri BCrypt ile hashler, JWT token üretir. |
| **Content Service** | Spring Boot, OpenFeign | Film verilerinin CRUD işlemlerini yönetir. Film detay sorgusunda Feign Client ile Review Service'ten yorumları çeker. |
| **Review Service** | Spring Boot | Kullanıcı yorumlarını ve puanlamalarını yönetir. Puan aralığı (0–10) ve boş yorum kontrolü yapar. |
| **MongoDB (×4)** | MongoDB 7.0 | Her servis için izole veritabanı: `auth_db`, `content_db`, `review_db`, `dispatcher_db`. |
| **Prometheus** | Prometheus | Tüm servislerin `/actuator/prometheus` endpointlerinden 15 saniyelik aralıklarla metrik toplar. |
| **Grafana** | Grafana | Prometheus verilerini görselleştirir; JVM bellek, HTTP istek sayıları, yanıt süreleri gibi metrikleri dashboard üzerinde sunar. |
| **Locust** | Python (Locust) | Sanal kullanıcılarla otomatik yük testi yapar: kayıt → giriş → film ekleme → yorum yazma → detay sorgulama senaryolarını çalıştırır. |

### 3.4 Docker Konteyner Yapısı

```mermaid
graph LR
    subgraph "Docker Compose Ortamı"
        subgraph "External Network"
            D["dispatcher:8080"]
            G["grafana:3000"]
        end
        subgraph "Internal Network"
            A["auth-service:8081"]
            CS["content-service:8082"]
            RS["review-service:8083"]
            MA[("mongo-auth")]
            MC[("mongo-content")]
            MR[("mongo-review")]
            MD[("mongo-dispatcher")]
            P["prometheus:9090"]
        end
    end
```

---

## 4. Uygulama, Ekran Görüntüleri ve Test Sonuçları

### 4.1 Ekran Görüntüleri

- **Kullanıcı Kayıt İsteği (POST /auth/register):**  
  ![](screenshoots/kullanici_kayit.png)

- **Kullanıcı Giriş ve Token Alma (POST /auth/login):**  
  ![](screenshoots/kullanici_giris.png)

- **Film Listeleme (GET /api/films):**  
  ![](screenshoots/tum_filmler.png)

- **Film Ekleme (POST /api/films):**  
  ![](screenshoots/film_ekle.png)

- **Film Detay + Yorumlar (GET /api/films/{id}/details):**  
  ![](screenshoots/film_detay.png)

- **Yorum Ekleme (POST /api/reviews):**  
  ![](screenshoots/yorum_ve_puan_ekle.png)

- **Grafana Dashboard:**  
  `[EKRAN GÖRÜNTÜSÜ EKLE — Servis metrikleri gösterimi]`

- **Locust Yük Testi Sonuçları:**  
  ![](screenshoots/locust.png)

### 4.2 Test Senaryoları

#### Birim Testleri (Unit Tests)

Dispatcher servisinde JUnit 5 ve Mockito kullanılarak TDD yaklaşımıyla birim testleri yazılmıştır:

| Test Sınıfı | Test Metodu | Açıklama | Beklenen Sonuç |
|---|---|---|---|
| `RoutingServiceTest` | `should_route_content_request_to_content_service` | `/api/films/123` URI'si Content Service'e yönlendirilmeli | `http://content-service:8082/api/films/123` |
| `RoutingServiceTest` | `should_route_review_request_to_review_service` | `/api/reviews/123` URI'si Review Service'e yönlendirilmeli | `http://review-service:8083/api/reviews/123` |
| `RoutingServiceTest` | `should_route_auth_request_to_auth_service` | `/auth/login` URI'si Auth Service'e yönlendirilmeli | `http://auth-service:8081/auth/login` |
| `RoutingServiceTest` | `should_return_null_for_unknown_path` | Bilinmeyen URI için null dönmeli | `null` |
| `AuthFilterTest` | `should_pass_auth_endpoints_without_token` | `/auth/*` endpointleri token olmadan geçmeli | `filterChain.doFilter()` çağrılır |
| `AuthFilterTest` | `should_reject_request_without_token` | Token yoksa 401 dönmeli | `sendError(401)` |
| `AuthFilterTest` | `should_reject_request_with_invalid_token` | Geçersiz token ile 403 dönmeli | `sendError(403)` |
| `ProxyServiceTest` | `should_forward_request_and_return_response` | İstek başarıyla iletilmeli | HTTP 200 |
| `ProxyServiceTest` | `should_return_503_when_service_unavailable` | Servis erişilemezse 503 dönmeli | HTTP 503 |

---

## 5. Sonuç ve Tartışma

### Başarılar
- **Mikroservis mimarisi** başarıyla uygulanmış; Auth, Content, Review ve Dispatcher olmak üzere dört bağımsız servis geliştirilmiştir.
- **Database-per-Service** prensibi ile her servisin veritabanı izole edilmiş (`auth_db`, `content_db`, `review_db`, `dispatcher_db`), servisler arası veri bağımlılığı ortadan kaldırılmıştır.
- **JWT tabanlı kimlik doğrulama** ile güvenli erişim sağlanmış, Dispatcher üzerindeki AuthFilter sayesinde merkezi yetkilendirme gerçekleştirilmiştir.
- **RESTful API** tasarımında Richardson Olgunluk Modeli Seviye 2 başarıyla uygulanmış; kaynaklar ayrıştırılmış ve HTTP metodları doğru amaçlarına uygun kullanılmıştır.
- **Docker Compose** ile tüm servisler, veritabanları ve izleme araçları tek komutla ayağa kaldırılabilmektedir.
- **Prometheus ve Grafana** entegrasyonu ile sistem metrikleri anlık olarak izlenebilmektedir.
- **Birim testleri** (JUnit 5 + Mockito) ile Dispatcher'ın kritik bileşenleri (routing, auth filter, proxy) test edilmiştir.
- **Yük testi** (Locust) ile sistemin eşzamanlı kullanıcı yükü altındaki performansı doğrulanmıştır.

### Sınırlılıklar
- Mevcut sistemde öneri algoritması bulunmamaktadır; film listeleme tür bazlı filtreleme ile sınırlıdır.
- Richardson Olgunluk Modeli'nin Seviye 3 (HATEOAS) katmanı uygulanmamıştır.
- Servisler arası iletişimde hata durumunda devre kesici (Circuit Breaker) mekanizması yoktur.
- Dağıtık veritabanlarında çapraz servis işlemleri için Saga Pattern gibi bir transactional yönetim mekanizması bulunmamaktadır.
- Frontend (kullanıcı arayüzü) geliştirilmemiştir; tüm testler Postman ve Locust üzerinden gerçekleştirilmiştir.

### Olası Geliştirmeler
- **Collaborative Filtering** veya **Content-Based Filtering** gibi makine öğrenmesi algoritmaları ile kişiselleştirilmiş film önerisi sunulabilir.
- **Resilience4j** ile Circuit Breaker ve Retry mekanizmaları eklenerek hata toleransı artırılabilir.
- **HATEOAS** desteği eklenerek API Seviye 3 olgunluğuna ulaşılabilir.
- **Redis** ile sık erişilen verilerin önbelleğe alınması performansı iyileştirebilir.
- **Kubernetes (K8s)** ile otomatik ölçekleme ve orkestrasyon sağlanabilir.
- **React/Angular** tabanlı bir frontend geliştirilerek kullanıcı deneyimi iyileştirilebilir.
