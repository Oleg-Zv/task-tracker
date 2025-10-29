const API_URL = "http://217.114.14.212:8080";

function getToken() {
    return localStorage.getItem("jwt");
}

$(document).ready(function () {
    if (!getToken()) {
        alert("Войдите в систему, чтобы продолжить.");
        window.location.href = "login.html";
        return;
    }

    const email = localStorage.getItem("email") || "Гость";
    $("#user-email").text(email);

    // Навигация
    $("#logout-btn").click(logout);
    $("#my-tasks-btn").click(() => window.location.href = "tasks.html");
    $("#add-task-page-btn").click(() => window.location.href = "add-task.html");
    $("#profile-btn").click(loadProfile);
});

window.addEventListener('resize', () => {
    document.body.style.height = window.innerHeight + 'px';
});

async function loadProfile() {
    try {
        const res = await fetch(`${API_URL}/app/v1/users/current`, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + getToken(),
                "Content-Type": "application/json"
            }
        });

        if (!res.ok) throw new Error("Невозможно загрузить профиль");

        const data = await res.json();
        alert(`🚀 Профиль:\nId: ${data.id}\nRole: ${data.role}\nEmail: ${data.email}`);
    } catch (err) {
        console.error(err);
        alert("Ошибка загрузки профиля. Возможно, токен недействителен.");
        logout();
    }
}

function logout() {
    $("#logout-btn").prop("disabled", true).text("Выход...");
    setTimeout(() => {
        localStorage.removeItem("jwt");
        localStorage.removeItem("email");
        window.location.href = "login.html";
    }, 300);
}

