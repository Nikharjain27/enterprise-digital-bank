import axiosClient from "../api/axiosClient";
import { API_ENDPOINTS } from "../api/endpoints";

export const loginApi = async (username: string, password: string) => {
  const response = await axiosClient.post(
    API_ENDPOINTS.AUTH.LOGIN,
    {
      username,
      password,
    }
  );

  return response.data;
};