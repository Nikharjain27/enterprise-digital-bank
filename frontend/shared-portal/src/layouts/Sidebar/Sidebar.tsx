import {
  Box,
  List,
  ListItemButton,
  ListItemText,
  Typography,
} from "@mui/material";

import { useNavigate } from "react-router-dom";

const Sidebar = () => {

  const navigate = useNavigate();

  return (
    <Box
      sx={{
        width: 250,
        height: "100vh",
        backgroundColor: "#0A2540",
        color: "white",
        padding: 2,
      }}
    >
      <Typography
        variant="h6"
        sx={{
          mb: 4,
          fontWeight: "bold",
        }}
      >
        Digital Bank
      </Typography>

      <List>

        <ListItemButton
          onClick={() => navigate("/dashboard")}
        >
          <ListItemText primary="Dashboard" />
        </ListItemButton>

        <ListItemButton
          onClick={() => navigate("/accounts")}
        >
          <ListItemText primary="Accounts" />
        </ListItemButton>

        <ListItemButton
          onClick={() => navigate("/transfers")}
        >
          <ListItemText primary="Transfers" />
        </ListItemButton>

        <ListItemButton
          onClick={() => navigate("/beneficiaries")}
        >
          <ListItemText primary="Beneficiaries" />
        </ListItemButton>

      </List>
    </Box>
  );
};

export default Sidebar;