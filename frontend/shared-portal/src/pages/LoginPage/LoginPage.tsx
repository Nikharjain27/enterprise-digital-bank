import {
  Box,
  Button,
  Container,
  Paper,
  TextField,
  Typography,
} from "@mui/material";

const LoginPage = () => {
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

          <TextField fullWidth label="Username" margin="normal" />

          <TextField
            fullWidth
            label="Password"
            type="password"
            margin="normal"
          />

          <Button variant="contained" fullWidth size="large" sx={{ mt: 3 }}>
            Login
          </Button>
        </Paper>
      </Box>
    </Container>
  );
};

export default LoginPage;
