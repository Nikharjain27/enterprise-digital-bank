import { useNavigate } from "react-router-dom";

import { useAppDispatch } from "../redux/hooks";

import { logout } from "./authSlice";

import { removeAccessToken } from "./jwtUtils";

export const useAuth = () => {

  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const logoutUser = () => {

    removeAccessToken();

    dispatch(logout());

    navigate("/login");
  };

  return {
    logoutUser,
  };
};