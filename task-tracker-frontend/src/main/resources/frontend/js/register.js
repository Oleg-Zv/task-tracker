const API_URL = "http://localhost:8080";

$(function () {
    $("#register-btn").click(function (e) {
        e.preventDefault();
        register();
    });
});

function register() {
    const firstname = $("#reg-firstname").val();
    const lastname = $("#reg-lastname").val();
    const email = $("#reg-email").val();
    const password = $("#reg-password").val();
    const repeat = $("#reg-repeat").val();

    if (!firstname || !lastname || !email || !password || !repeat) {
        alert("Заполни все поля!");
        return;
    }

    if (password !== repeat) {
        alert("Пароли не совпадают!");
        return;
    }

    $.ajax({
        url: `${API_URL}/auth/signup`,
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            firstname,
            lastname,
            email,
            password,
            confirmPassword: repeat,
            role: "USER"
        }),
        success: function (res) {
            localStorage.setItem("jwt", res.jwtToken);
            localStorage.setItem("email", email);
            window.location.href = "index.html";
        },
        error: function (xhr) {
            alert("Ошибка регистрации: " + xhr.responseText);
        }
    });
}
