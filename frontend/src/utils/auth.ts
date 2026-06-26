import type { Rol } from "../types/rol";
import type { User } from "../types/user";
import { replace } from "./routes";

export const getAuthorizedUser = (requiredRol: Rol): User | null => {
  const rawUser = localStorage.getItem("auth-user");
  const user: User | null = rawUser ? JSON.parse(rawUser) : null;

  if (user?.rol !== requiredRol) {
    localStorage.removeItem("auth-user");
    replace("login");
    return null;
  }

  return user;
};

export const logOut = () => {
  localStorage.removeItem("auth-user");
  replace("login");
};
