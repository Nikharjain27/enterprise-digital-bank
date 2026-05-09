import {
  AppBar,
  Box,
  Button,
  Toolbar,
  Typography,
} from "@mui/material";

import { useAuth } from "../../auth/useAuth";

const Header = () => {

  const { logoutUser } = useAuth();

  return (
    <AppBar position="static">

      <Toolbar>

        <Typography
          variant="h6"
          sx={{ flexGrow: 1 }}
        >
          Enterprise Digital Bank
        </Typography>

        <Box>
          <Button
            color="inherit"
            onClick={logoutUser}
          >
            Logout
          </Button>
        </Box>

      </Toolbar>

    </AppBar>
  );
};

export default Header;