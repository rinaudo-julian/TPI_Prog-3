import type { ErrorResponse } from "../../../types/error";

const API_BASE_URL = "http://localhost:8080";

const form = document.getElementById("register-form") as HTMLFormElement;
const message = document.getElementById("form-message") as HTMLDivElement;

form.addEventListener("submit", async (event: SubmitEvent) => {
  event.preventDefault();
  hideMessage();

  const formData = new FormData(form);

  const payload = {
    nombre: String(formData.get("nombre") ?? "").trim(),
    apellido: String(formData.get("apellido") ?? "").trim(),
    email: String(formData.get("email") ?? "").trim(),
    celular: String(formData.get("celular") ?? "").trim(),
    password: String(formData.get("password") ?? "")
  };

  try {
    const response = await fetch(`${API_BASE_URL}/usuarios`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const res = (await response.json()) as ErrorResponse;
      showMessage(res.message, "error");
      return;
    }

    showMessage("Usuario registrado correctamente", "success");
    form.reset();
  } catch {
    showMessage("No se pudo conectar con el servidor", "error");
  }
});

const hideMessage = () => {
  message.textContent = "";
  message.className =
    "hidden rounded-md border px-3 py-2 text-[13px] font-medium";
};

const showMessage = (text: string, type: "error" | "success") => {
  message.textContent = text;
  message.className =
    type === "error"
      ? "rounded-md border border-danger/30 bg-danger/10 px-3 py-2 text-[13px] font-medium text-danger"
      : "rounded-md border border-success/30 bg-success/10 px-3 py-2 text-[13px] font-medium text-success";
};
