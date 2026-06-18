import "./style.css";
import type { User } from "./types/user";
import { Rol } from "./types/rol";
import { replace } from "./utils/routes";

const rawUser = localStorage.getItem("auth-user");

const authUser: User | null = rawUser ? JSON.parse(rawUser) : null;

!authUser?.id
  ? replace("login")
  : authUser.rol === Rol.USUARIO
    ? replace("home")
    : null;
