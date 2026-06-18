import { Rol } from "../../../types/rol";
import { getAuthorizedUser, logOut } from "../../../utils/auth";

const user = getAuthorizedUser(Rol.USUARIO);
const logoutButton = document.getElementById("logout-button") as HTMLButtonElement;

if (user) {
  const userName = document.getElementById("user-name") as HTMLSpanElement;
  userName.textContent = `${user.nombre} ${user.apellido}`;
}

logoutButton.addEventListener("click", logOut);
