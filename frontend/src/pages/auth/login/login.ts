import type { ErrorResponse } from "../../../types/error";
import { Rol } from "../../../types/rol";
import type { User } from "../../../types/user";
import { replace } from "../../../utils/routes";

const API_BASE_URL = "http://localhost:8080";

const form = document.getElementById("login-form") as HTMLFormElement;
const message = document.getElementById("form-message") as HTMLDivElement;

const showMessage = (text: string, type: "error" | "success") => {
  message.textContent = text;
  message.className =
    type === "error"
      ? "rounded-md border border-danger/30 bg-danger/10 px-3 py-2 text-[13px] font-medium text-danger"
      : "rounded-md border border-success/30 bg-success/10 px-3 py-2 text-[13px] font-medium text-success";
};

const hideMessage = () => {
  message.textContent = "";
  message.className =
    "hidden rounded-md border px-3 py-2 text-[13px] font-medium";
};

form.addEventListener("submit", async (event: SubmitEvent) => {
  event.preventDefault();
  hideMessage();

  const formData = new FormData(form);
  const payload = {
    email: String(formData.get("email") ?? "").trim(),
    password: String(formData.get("password") ?? "")
  };

  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const error = (await response.json()) as ErrorResponse;
      showMessage(error.message, "error");
      return;
    }
    const authUser = (await response.json()) as User;
    localStorage.setItem("auth-user", JSON.stringify(authUser));

    if (authUser.rol === Rol.USUARIO) {
      replace("home");
    }
  } catch {
    showMessage("No se pudo conectar con el servidor", "error");
  }
});
