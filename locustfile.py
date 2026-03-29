from locust import HttpUser, task, between

class FilmPlatformUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def get_film_details(self):
        self.client.get("/api/films/1/details")