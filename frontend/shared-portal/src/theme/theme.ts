import { createTheme } from "@mui/material/styles";

export const appTheme = createTheme({
  palette: {
    primary: {
      main: "#0A2540",
    },
    secondary: {
      main: "#00A86B",
    },
    background: {
      default: "#F5F7FA",
    },
  },
  typography: {
    fontFamily: "Inter, Roboto, sans-serif",
  },
  shape: {
    borderRadius: 10,
  },
});