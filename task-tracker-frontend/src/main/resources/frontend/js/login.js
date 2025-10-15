const API_URL = "http://localhost:8080";

$(function () {
    $("#login-btn").click(function (e) {
        e.preventDefault();
        login();
    });
});

function login() {
    const email = $("#login-email").val();
    const password = $("#login-password").val();

    if (!email || !password) {
        alert("Заполни email и пароль!");
        return;
    }

    $.ajax({
        url: `${API_URL}/auth/login`,
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ email, password }),
        success: function (res) {
            localStorage.setItem("jwt", res.jwtToken);
            localStorage.setItem("email", email);
            window.location.href = "index.html";
        },
        error: function (xhr) {
            alert("Ошибка авторизации: " + xhr.responseText);
        }
    });
}
