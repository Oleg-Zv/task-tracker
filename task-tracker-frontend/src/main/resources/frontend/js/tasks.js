const API_URL = "http://localhost:8080";
let token = localStorage.getItem("jwt");
let email = localStorage.getItem("email");
let currentTaskId = null;
let currentTaskStatus = null; // <-- NEW: хранит статус открытой задачи ("DONE" / "PENDING")
let taskModal;

$(document).ready(function () {
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    $("#user-email").text(email || "");
    $("#logout-btn").click(logout);
    $("#add-task-btn").click(addTask);
    $("#back-btn").click(() => window.location.href = "index.html");

    // Инициализация глобальной модалки
    taskModal = new bootstrap.Modal($("#taskModal"));

    // Сохранение изменений при вводе (debounce можно добавить позже)
    $("#modal-task-title, #modal-task-desc").on("input", function () {
        if (!currentTaskId) return;
        saveTaskChanges();
    });

    // Удаление
    $("#modal-delete-btn").off("click").on("click", function () {
        if (!currentTaskId) return;
        deleteTask(currentTaskId);
    });

    // Переключение статуса через кнопку в модалке
    $("#modal-toggle-btn").off("click").on("click", function () {
        if (!currentTaskId) return;
        // используем currentTaskStatus, а не чекбокс
        const currentlyDone = currentTaskStatus === "DONE";
        toggleTaskDone(!currentlyDone);
    });

    loadTasks();
});

function loadTasks() {
    $.ajax({
        url: `${API_URL}/app/v1/tasks`,
        method: "GET",
        headers: { Authorization: "Bearer " + token },
        success: function (page) {
            const tasks = page.content || page;
            renderTasks(tasks);
        },
        error: function () {
            alert("Ошибка загрузки задач!");
            logout();
        }
    });
}

function renderTasks(tasks) {
    $("#pending-tasks").empty();
    $("#done-tasks").empty();

    tasks.forEach(t => {
        const li = $("<li>")
            .addClass("list-group-item")
            .text(t.title)
            .css("cursor", "pointer")
            .click(() => openTaskModal(t));
        if (t.status === "PENDING") $("#pending-tasks").append(li);
        else $("#done-tasks").append(li);
    });
}

function addTask() {
    const title = $("#new-task-title").val().trim();
    const description = $("#new-task-desc").val().trim();
    if (!title) { alert("Введите название задачи"); return; }

    const newTask = { title, description: description || " ", status: "PENDING" };

    $.ajax({
        url: `${API_URL}/app/v1/tasks`,
        method: "POST",
        headers: { Authorization: "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify(newTask),
        success: function (createdTask) {
            $("#new-task-title").val("");
            $("#new-task-desc").val("");

            const li = $("<li>")
                .addClass("list-group-item")
                .text(createdTask.title)
                .css("cursor", "pointer")
                .click(() => openTaskModal(createdTask));
            if (createdTask.status === "PENDING") $("#pending-tasks").append(li);
            else $("#done-tasks").append(li);

            showStatusToast("Задача добавлена!", true);
        },
        error: function (xhr) { alert("Ошибка при добавлении задачи: " + xhr.responseText); }
    });
}

function openTaskModal(task) {
    currentTaskId = task.id;
    currentTaskStatus = task.status; // <-- NEW: сохраняем статус открытой задачи
    $("#modal-task-title").val(task.title);
    $("#modal-task-desc").val(task.description || "");

    // Обновляем текст и стиль кнопки переключения статуса
    const toggleBtn = $("#modal-toggle-btn");
    if (currentTaskStatus === "DONE") {
        toggleBtn.html('<i class="bi bi-x-circle me-2"></i> Пометить не сделанной');
        toggleBtn.removeClass("btn-success").addClass("btn-warning");
    } else {
        toggleBtn.html('<i class="bi bi-check2-circle me-2"></i> Пометить сделанной');
        toggleBtn.removeClass("btn-warning").addClass("btn-success");
    }

    taskModal.show();
}

function saveTaskChanges() {
    if (!currentTaskId) return;
    const title = $("#modal-task-title").val().trim();
    const description = $("#modal-task-desc").val().trim() || " ";
    $.ajax({
        url: `${API_URL}/app/v1/tasks/${currentTaskId}`,
        method: "PUT",
        headers: { Authorization: "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ title, description }),
        success: loadTasks,
        error: function (xhr) { alert("Ошибка при сохранении: " + xhr.responseText); }
    });
}

function toggleTaskDone(done) {
    if (!currentTaskId) return;
    const url = done
        ? `${API_URL}/app/v1/tasks/${currentTaskId}/done`
        : `${API_URL}/app/v1/tasks/${currentTaskId}/pending`;

    $.ajax({
        url,
        method: "PUT",
        headers: { Authorization: "Bearer " + token },
        success: function(updatedTask) {
            // Обновляем локальную переменную статуса
            currentTaskStatus = updatedTask.status;

            // Обновим кнопку в модалке в соответствии с ответом
            const isDone = currentTaskStatus === "DONE";
            const toggleBtn = $("#modal-toggle-btn");
            if (isDone) {
                toggleBtn.html('<i class="bi bi-x-circle me-2"></i> Пометить не сделанной');
                toggleBtn.removeClass("btn-success").addClass("btn-warning");
            } else {
                toggleBtn.html('<i class="bi bi-check2-circle me-2"></i> Пометить сделанной');
                toggleBtn.removeClass("btn-warning").addClass("btn-success");
            }

            loadTasks();
            showStatusToast(isDone ? "Задача выполнена!" : "Задача не выполнена!", isDone);

            // Авто-закрытие модалки и очистка currentTaskId
            setTimeout(() => {
                taskModal.hide();
                currentTaskId = null;
                currentTaskStatus = null;
            }, 800);
        },
        error: function(xhr) { alert("Ошибка при изменении статуса: " + xhr.responseText); }
    });
}

function deleteTask(id) {
    if (!id) return;
    $.ajax({
        url: `${API_URL}/app/v1/tasks/${id}`,
        method: "DELETE",
        headers: { Authorization: "Bearer " + token },
        success: function () {
            loadTasks();
            currentTaskId = null;
            currentTaskStatus = null;
            taskModal.hide();
            showStatusToast("Задача удалена!", true);
        },
        error: function (xhr) {
            showStatusToast("Ошибка при удалении задачи!", false);
        }
    });
}

// === Всплывающее уведомление ===
function showStatusToast(message, success = true) {
    let toast = $("#status-toast");
    if (toast.length === 0) {
        toast = $('<div id="status-toast"></div>').appendTo("body");
        toast.css({
            position: "fixed",
            top: "20px",
            right: "20px",
            padding: "12px 18px",
            "border-radius": "8px",
            color: "#fff",
            "font-weight": "500",
            "z-index": 9999,
            display: "none",
        });
    }
    toast.stop(true, true).css("background-color", success ? "#059669" : "#dc2626");
    toast.text(message).fadeIn(200).delay(1200).fadeOut(200);
}

function logout() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("email");
    window.location.href = "login.html";
}
