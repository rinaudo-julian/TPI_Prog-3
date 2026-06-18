import "./style.css";
import type { User } from "./types/user";

const rawUser = localStorage.getItem("auth-user");

const authUser: User | null = rawUser ? JSON.parse(rawUser) : null;

!authUser?.id
  ? window.location.replace("src/pages/auth/login/login.html")
  : window.location.replace("src/pages/store/home/home.html");
