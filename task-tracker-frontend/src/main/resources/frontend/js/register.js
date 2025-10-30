const API_URL = "http://217.114.14.212:8080";
$(function () {
    $("#register-btn").click(function (e) {
        e.preventDefault();
        register();
    });
});

window.addEventListener('resize', () => {
    document.body.style.height = window.innerHeight + 'px';
});

function register() {
    const firstname = $("#reg-firstname").val().trim();
    const lastname = $("#reg-lastname").val().trim();
    const email = $("#reg-email").val().trim();
    const password = $("#reg-password").val();
    const repeat = $("#reg-repeat").val();

    if (!firstname || !lastname || !email || !password || !repeat) {
        alert("Заполни все поля!");
        return;
    }

    if (firstname.length < 2 || firstname.length > 20) {
        alert("Имя должно быть от 2 до 20 символов!");
        return;
    }

    if (lastname.length < 2 || lastname.length > 20) {
        alert("Фамилия должна быть от 2 до 20 символов!");
        return;
    }

    if (password.length < 6 || password.length > 30) {
        alert("Пароль должен быть от 6 до 30 символов!");
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
            let message = "Ошибка регистрации";

            if (xhr.status === 409) {
                message = "❌ Пользователь с таким email уже существует";
            }
            alert(message)
        }
    });
}
