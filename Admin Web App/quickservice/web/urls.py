from django.urls import path

from . import views

urlpatterns = [
    path("", views.index, name = "index" ),
    path("login", views.login_view, name = "login"),
    path("logout", views.logout_view, name = "logout"),
    path("charts", views.charts_view, name = "charts"),
    path("verifyclient", views.verify_client_view, name = "verifyclient"),
]