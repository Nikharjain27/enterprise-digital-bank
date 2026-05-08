import React from "react";
import ReactDOM from "react-dom/client";
import { ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import { Provider } from "react-redux";

import { appTheme } from "./theme/theme";
import { store } from "./redux/store";

import "./api/interceptors";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <Provider store={store}>
      <ThemeProvider theme={appTheme}>
        <CssBaseline />
        <div>Enterprise Banking Frontend Initialized</div>
      </ThemeProvider>
    </Provider>
  </React.StrictMode>
);