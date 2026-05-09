import {
  Box,
  Button,
  Container,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";

import { loginValidation } from "../../validations/loginValidation";
import { loginApi } from "../../auth/authService";

import { setAccessToken } from "../../auth/jwtUtils";

import { useAppDispatch } from "../../redux/hooks";
import { loginSuccess } from "../../auth/authSlice";

interface LoginFormData {
  username: string;
  password: string;
}

const LoginPage = () => {
  const navigate = useNavigate();

  const dispatch = useAppDispatch();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>();

  const onSubmit = async (data: LoginFormData) => {
    try {
      const response = await loginApi(
        data.username,
        data.password
      );

      setAccessToken(response.token);

      dispatch(loginSuccess(response.token));

      navigate("/dashboard");
    } catch (error) {
      console.error("Login failed", error);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          height: "100vh",
          display: "flex",
          alignItems: "center",
        }}
      >
        <Paper
          elevation={3}
          sx={{
            padding: 4,
            width: "100%",
            borderRadius: 3,
          }}
        >
          <Typography
            variant="h4"
            sx={{
              mb: 3,
              fontWeight: "bold",
            }}
          >
            Enterprise Digital Bank
          </Typography>

          <form onSubmit={handleSubmit(onSubmit)}>
            <TextField
              fullWidth
              label="Username"
              margin="normal"
              error={!!errors.username}
              helperText={errors.username?.message}
              {...register(
                "username",
                loginValidation.username
              )}
            />

            <TextField
              fullWidth
              label="Password"
              type="password"
              margin="normal"
              error={!!errors.password}
              helperText={errors.password?.message}
              {...register(
                "password",
                loginValidation.password
              )}
            />

            <Button
              type="submit"
              variant="contained"
              fullWidth
              size="large"
              sx={{ mt: 3 }}
            >
              Login
            </Button>
          </form>
        </Paper>
      </Box>
    </Container>
  );
};

export default LoginPage;