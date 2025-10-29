const API_URL = "http://217.114.14.212:8080";

$(function () {
    $("#login-btn").click(function (e) {
        e.preventDefault();
        login();
    });
});

window.addEventListener('resize', () => {
    document.body.style.height = window.innerHeight + 'px';
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
        data: JSON.stringify({ email, rawPassword: password }),
        success: function (res) {
            localStorage.setItem("jwt", res.jwtToken);
            localStorage.setItem("email", email);
            window.location.href = "index.html";
        },
        error: function (xhr) {
            const status = xhr.status;
            let message = "Ошибка авторизации";

            if (status === 401) {
                message = "❌ Неверный email или пароль";
            } else if (status === 404) {
                message = "❌ Пользователь не найден";
            }
            alert(message)
        }

    });

}
