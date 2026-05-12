import {
  Button,
  Paper,
  Stack,
  Typography,
} from "@mui/material";

import { useNavigate }
from "react-router-dom";

const QuickActions = () => {

  const navigate = useNavigate();

  return (

    <Paper
      sx={{
        padding: 3,
        borderRadius: 4,
      }}
    >

      <Typography
        variant="h6"
        sx={{ mb: 2 }}
      >
        Quick Actions
      </Typography>

      <Stack spacing={2}>

        <Button
          variant="contained"
          onClick={() =>
            navigate("/transfers")
          }
        >
          Transfer Money
        </Button>

        <Button
          variant="outlined"
          onClick={() =>
            navigate("/beneficiaries")
          }
        >
          Manage Beneficiaries
        </Button>

        <Button
          variant="outlined"
          onClick={() =>
            navigate("/transactions")
          }
        >
          View Transactions
        </Button>

      </Stack>

    </Paper>
  );
};

export default QuickActions;