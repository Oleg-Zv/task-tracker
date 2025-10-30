const API_URL = "http://217.114.14.212:8080";

let token = localStorage.getItem("jwt");
let email = localStorage.getItem("email");
let currentTaskId = null;
let currentTaskStatus = null;
let taskModal;
let originalTitle = "";
let originalDesc = "";

$(document).ready(function () {
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    $("#user-email").text(email || "");
    $("#logout-btn").click(logout);
    $("#add-task-btn").click(addTask);
    $("#back-btn").click(() => window.location.href = "index.html");

    taskModal = new bootstrap.Modal($("#taskModal"));

    $("#modal-delete-btn").click(() => currentTaskId && deleteTask(currentTaskId));
    $("#modal-toggle-btn").click(() => currentTaskId && toggleTaskDone(currentTaskStatus !== "DONE"));
    $("#modal-edit-btn").click(enableEditing);
    $("#modal-save-btn").click(saveTaskChanges);
    $("#modal-cancel-btn").click(cancelEditing);

    $("#toggle-done").click(function() {
        $("#done-tasks").collapse('toggle');
        $(this).toggleClass("bi-caret-down-fill bi-caret-up-fill");
    });

    $("#toggle-pending").click(function() {
        $("#pending-tasks").collapse('toggle');
        $(this).toggleClass("bi-caret-down-fill bi-caret-up-fill");
    });

    $("#done-tasks, #pending-tasks").collapse('hide');

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

    if (!title) { showStatusToast("Введите название задачи", false); return; }
    if (!description) { showStatusToast("Введите описание задачи", false); return; }

    const newTask = { title, description, status: "PENDING" };

    $.ajax({
        url: `${API_URL}/app/v1/tasks`,
        method: "POST",
        headers: { Authorization: "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify(newTask),
        success: function (createdTask) {
            $("#new-task-title, #new-task-desc").val("");
            loadTasks();

            if (createdTask.status === "PENDING") {
                $("#pending-tasks").collapse('show');
                $("#toggle-pending").removeClass("bi-caret-down-fill").addClass("bi-caret-up-fill");
            } else {
                $("#done-tasks").collapse('show');
                $("#toggle-done").removeClass("bi-caret-down-fill").addClass("bi-caret-up-fill");
            }

            showStatusToast("Задача добавлена!", true);
        },
        error: function (xhr) {
            showStatusToast("Ошибка при добавлении задачи: " + xhr.responseText, false);
        }
    });
}

function openTaskModal(task) {
    currentTaskId = task.id;
    currentTaskStatus = task.status;
    originalTitle = task.title || "";
    originalDesc = task.description || "";

    $("#modal-task-title").val(originalTitle).prop("readonly", true);
    $("#modal-task-desc").val(originalDesc).prop("readonly", true);

    $("#modal-edit-btn").removeClass("d-none");
    $("#modal-save-btn, #modal-cancel-btn").addClass("d-none");

    updateToggleBtn();
    taskModal.show();
}

function enableEditing() {
    $("#modal-task-title, #modal-task-desc").prop("readonly", false);
    $("#modal-edit-btn").addClass("d-none");
    $("#modal-save-btn, #modal-cancel-btn").removeClass("d-none");
}

function saveTaskChanges() {
    if (!currentTaskId) return;

    const title = $("#modal-task-title").val().trim();
    const desc = $("#modal-task-desc").val().trim();

    if (!title) { showStatusToast("Название не может быть пустым!", false); return; }
    if (!desc) { showStatusToast("Описание не может быть пустым!", false); return; }

    $.ajax({
        url: `${API_URL}/app/v1/tasks/${currentTaskId}`,
        method: "PUT",
        headers: { Authorization: "Bearer " + token },
        contentType: "application/json",
        data: JSON.stringify({ title, description: desc }),
        success: function () {
            showStatusToast("Задача изменена!", true);
            loadTasks();
            taskModal.hide();
            resetModalState();
        },
        error: function (xhr) {
            showStatusToast("Ошибка при сохранении: " + xhr.responseText, false);
        }
    });
}

function cancelEditing() {
    $("#modal-task-title").val(originalTitle).prop("readonly", true);
    $("#modal-task-desc").val(originalDesc).prop("readonly", true);
    $("#modal-edit-btn").removeClass("d-none");
    $("#modal-save-btn, #modal-cancel-btn").addClass("d-none");
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
        success: function (updatedTask) {
            currentTaskStatus = updatedTask.status;
            updateToggleBtn();
            loadTasks();
            showStatusToast(currentTaskStatus === "DONE" ? "Задача выполнена!" : "Задача не выполнена!", currentTaskStatus === "DONE");

            setTimeout(() => {
                taskModal.hide();
                resetModalState();
            }, 800);
        },
        error: function (xhr) {
            showStatusToast("Ошибка при изменении статуса: " + xhr.responseText, false);
        }
    });
}

function updateToggleBtn() {
    const toggleBtn = $("#modal-toggle-btn");
    if (currentTaskStatus === "DONE") {
        toggleBtn.html('<i class="bi bi-x-circle me-2"></i> Пометить не сделанной')
            .removeClass("btn-success").addClass("btn-warning");
    } else {
        toggleBtn.html('<i class="bi bi-check2-circle me-2"></i> Пометить сделанной')
            .removeClass("btn-warning").addClass("btn-success");
    }
}

function deleteTask(id) {
    if (!id) return;
    $.ajax({
        url: `${API_URL}/app/v1/tasks/${id}`,
        method: "DELETE",
        headers: { Authorization: "Bearer " + token },
        success: function () {
            loadTasks();
            taskModal.hide();
            resetModalState();
            showStatusToast("Задача удалена!", true);
        },
        error: function () {
            showStatusToast("Ошибка при удалении задачи!", false);
        }
    });
}

function resetModalState() {
    currentTaskId = null;
    currentTaskStatus = null;
    originalTitle = "";
    originalDesc = "";
}

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

window.addEventListener('resize', () => {
    document.body.style.height = window.innerHeight + 'px';
});
