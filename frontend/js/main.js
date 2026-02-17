const API_URL = "http://localhost:8080/api/participants";
const ATTENDANCE_API_URL = "http://localhost:8080/api/attendance";

/* ================= LOAD PARTICIPANTS ================= */
function loadParticipants() {
    const table = document.getElementById("participantTableBody");
    if (!table) return;

    fetch(API_URL)
        .then(res => res.json())
        .then(data => {
            table.innerHTML = "";

            data.forEach(p => {
                const statusClass = p.status.toLowerCase();
                const statusBadge = `<span class="status-badge ${statusClass}">${p.status}</span>`;
                const notesDisplay = p.notes ? `<span class="notes-cell" title="${p.notes}">${p.notes}</span>` : '<span class="text-muted">-</span>';
                
                const row = `
                    <tr>
                        <td><strong>${p.name}</strong></td>
                        <td>${p.email}</td>
                        <td>${p.programName || '<span class="text-muted">-</span>'}</td>
                        <td><span class="badge bg-secondary">${p.cohortNumber || '-'}</span></td>
                        <td>${statusBadge}</td>
                        <td>${notesDisplay}</td>
                        <td class="text-center">
                            <button onclick="editParticipant(${p.id})" class="btn btn-warning btn-sm">✏️ Edit</button>
                            <button onclick="deleteParticipant(${p.id})" class="btn btn-danger btn-sm">🗑️ Delete</button>
                        </td>
                    </tr>`;
                table.innerHTML += row;
            });
        })
        .catch(err => console.error("Fetch error:", err));
}

/* ================= ATTENDANCE HELPERS ================= */
function getSelectedAttendanceDate() {
    const input = document.getElementById("attendanceDate");
    if (!input) return null;

    if (!input.value) {
        const today = new Date().toISOString().slice(0, 10);
        input.value = today;
    }
    return input.value;
}

/* ================= LOAD ATTENDANCE TABLE ================= */
function loadAttendance() {
    const table = document.getElementById("attendanceTableBody");
    if (!table) return;

    const date = getSelectedAttendanceDate();
    if (!date) return;

    // First load all participants into the table
    fetch(API_URL)
        .then(res => res.json())
        .then(participants => {
            table.innerHTML = "";

            participants.forEach(p => {
                const row = `
                    <tr>
                        <td><strong>${p.name}</strong><br><small class="text-muted">${p.email}</small></td>
                        <td class="text-center">
                            <input type="checkbox" data-id="${p.id}" class="form-check-input" style="width: 24px; height: 24px;" unchecked>
                        </td>
                    </tr>`;
                table.innerHTML += row;
            });

            // Then mark those who are already present for that date
            console.log("Fetching attendance for date:", date);
            return fetch(ATTENDANCE_API_URL + "/" + date);
        })
        .then(res => {
            if (!res.ok) {
                console.warn("Failed to fetch attendance, status:", res.status);
                return [];
            }
            return res.json();
        })
        .then(presentIds => {
            console.log("Received present participant IDs:", presentIds);
            if (!presentIds || !Array.isArray(presentIds)) {
                console.warn("Invalid presentIds:", presentIds);
                return;
            }
            
            // Convert all IDs to strings for comparison
            const presentIdsStr = presentIds.map(id => String(id));
            console.log("Looking for checkboxes with IDs:", presentIdsStr);
            
            // Get all checkboxes and check them if their ID matches
            const checkboxes = table.querySelectorAll('input[type="checkbox"][data-id]');
            checkboxes.forEach(cb => {
                const cbId = String(cb.getAttribute("data-id"));
                if (presentIdsStr.includes(cbId)) {
                    console.log("Marking checkbox as checked for participant ID:", cbId);
                    cb.checked = true;
                } else {
                    cb.checked = false; // Ensure unchecked if not in list
                }
            });
        })
        .catch(err => console.error("Attendance fetch error:", err));
}

/* ================= SAVE ATTENDANCE ================= */
function saveAttendance() {
    const table = document.getElementById("attendanceTableBody");
    const date = getSelectedAttendanceDate();
    if (!table || !date) return;

    const checkedBoxes = Array.from(table.querySelectorAll("input[type='checkbox']:checked"));
    const ids = checkedBoxes.map(cb => {
        const idStr = cb.getAttribute("data-id");
        const idNum = parseInt(idStr, 10);
        if (isNaN(idNum)) {
            console.error("Invalid participant ID:", idStr);
            return null;
        }
        return idNum;
    }).filter(id => id !== null);
    
    console.log("Saving attendance - checked participant IDs:", ids);

    const saveBtn = document.getElementById("saveAttendanceBtn");
    const originalText = saveBtn.textContent;
    saveBtn.disabled = true;
    saveBtn.textContent = "Saving...";
    saveBtn.classList.add("loading");

    console.log("Saving attendance for date:", date, "Participant IDs:", ids);
    
    fetch(ATTENDANCE_API_URL + "/" + date, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(ids)
    })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(`HTTP ${res.status}: ${text}`);
                });
            }
            return res.text();
        })
        .then(message => {
            console.log("Attendance saved successfully:", message);
            showSuccessMessage(`Attendance saved successfully for ${date}!`);
            saveBtn.disabled = false;
            saveBtn.textContent = originalText;
            saveBtn.classList.remove("loading");
        })
        .catch(err => {
            console.error("Save attendance error:", err);
            alert("Error saving attendance: " + err.message + "\n\nPlease check the browser console for details.");
            saveBtn.disabled = false;
            saveBtn.textContent = originalText;
            saveBtn.classList.remove("loading");
        });
}
/* ================= DELETE ================= */
function deleteParticipant(id) {
    if (!confirm("Are you sure you want to delete this participant?")) {
        return;
    }
    
    fetch(API_URL + "/" + id, { method: "DELETE" })
        .then(() => {
            showSuccessMessage("Participant deleted successfully!");
            loadParticipants();
        })
        .catch(err => {
            console.error("Delete error:", err);
            alert("Error deleting participant. Please try again.");
        });
}

/* ================= SUCCESS MESSAGE ================= */
function showSuccessMessage(message) {
    // Remove existing message if any
    const existing = document.querySelector(".success-message");
    if (existing) existing.remove();
    
    const messageDiv = document.createElement("div");
    messageDiv.className = "success-message";
    messageDiv.textContent = message;
    
    const container = document.querySelector(".content-area") || document.querySelector(".container");
    if (container) {
        container.insertBefore(messageDiv, container.firstChild);
        
        // Auto remove after 3 seconds
        setTimeout(() => {
            messageDiv.style.opacity = "0";
            messageDiv.style.transform = "translateY(-10px)";
            setTimeout(() => messageDiv.remove(), 300);
        }, 3000);
    }
}

/* ================= EDIT NAVIGATION ================= */
function editParticipant(id) {
    window.location.href = "participant-form.html?id=" + id;
}

/* ================= DASHBOARD STATS ================= */
function loadDashboardStats() {
    const total = document.getElementById("totalCount");
    if (!total) return;

    fetch(API_URL)
        .then(res => res.json())
        .then(data => {
            document.getElementById("totalCount").innerText = data.length;
            document.getElementById("activeCount").innerText = data.filter(p => p.status === "Active").length;
            document.getElementById("completedCount").innerText = data.filter(p => p.status === "Completed").length;
            document.getElementById("droppedCount").innerText = data.filter(p => p.status === "Dropped").length;
        });
}

/* ================= FORM SETUP (CREATE + UPDATE) ================= */
function setupForm() {
    const form = document.getElementById("participantForm");
    if (!form) return;

    const params = new URLSearchParams(window.location.search);
    const editId = params.get("id");

    /* If editing, load existing data */
    if (editId) {
        fetch(API_URL + "/" + editId)
            .then(res => res.json())
            .then(p => {
                document.querySelector("[name='name']").value = p.name;
                document.querySelector("[name='email']").value = p.email;
                document.querySelector("[name='phone']").value = p.phone;
                document.querySelector("[name='program']").value = p.programName;
                document.querySelector("[name='cohort']").value = p.cohortNumber;
                document.querySelector("[name='status']").value = p.status;
                document.querySelector("[name='notes']").value = p.notes || "";
            });
    }

    /* Form submit handler */
    form.addEventListener("submit", function(e) {
        e.preventDefault();

        const participant = {
            name: document.querySelector("[name='name']").value,
            email: document.querySelector("[name='email']").value,
            phone: document.querySelector("[name='phone']").value,
            programName: document.querySelector("[name='program']").value,
            cohortNumber: document.querySelector("[name='cohort']").value,
            paymentStatus: "Paid",
            status: document.querySelector("[name='status']").value,
            notes: document.querySelector("[name='notes']").value
        };

        const method = editId ? "PUT" : "POST";
        const url = editId ? API_URL + "/" + editId : API_URL;

        const submitBtn = form.querySelector("button[type='submit']");
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = "Saving...";
        
        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(participant)
        })
            .then(() => {
                showSuccessMessage(editId ? "Participant updated successfully!" : "Participant added successfully!");
                setTimeout(() => window.location.href = "participants.html", 1000);
            })
            .catch(err => {
                console.error("Save error:", err);
                alert("Error saving participant. Please try again.");
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            });
    });
}

/* ================= RUN PAGE LOGIC ================= */
document.addEventListener("DOMContentLoaded", function () {
    loadParticipants();
    loadDashboardStats();
    loadAttendance();
    setupForm();

    const dateInput = document.getElementById("attendanceDate");
    if (dateInput) {
        dateInput.addEventListener("change", loadAttendance);
    }

    const saveBtn = document.getElementById("saveAttendanceBtn");
    if (saveBtn) {
        saveBtn.addEventListener("click", function () {
            saveAttendance();
        });
    }
});
