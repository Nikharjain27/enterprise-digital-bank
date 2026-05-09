import { Navigate } from "react-router-dom";

import { useAppSelector } from "../redux/hooks";

import { getAccessToken } from "../auth/jwtUtils";

interface Props {
  children: React.ReactElement;
}

const ProtectedRoute = ({ children }: Props) => {

  const isAuthenticated = useAppSelector(
    (state) => state.auth.isAuthenticated
  );

  const token = getAccessToken();

  if (!isAuthenticated && !token) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;