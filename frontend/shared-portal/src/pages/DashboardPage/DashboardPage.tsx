import {
  Grid,
  Paper,
  Typography,
} from "@mui/material";

import DashboardLayout from "../../layouts/DashboardLayout/DashboardLayout";

const DashboardPage = () => {

  return (
    <DashboardLayout>

      <Typography
        variant="h4"
        sx={{ mb: 3 }}
      >
        Banking Dashboard
      </Typography>

      <Grid container spacing={3}>

        <Grid size={{ xs: 12, md: 4 }}>

          <Paper sx={{ padding: 3 }}>
            <Typography variant="h6">
              Total Balance
            </Typography>

            <Typography variant="h4">
              ₹ 2,45,000
            </Typography>
          </Paper>

        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>

          <Paper sx={{ padding: 3 }}>
            <Typography variant="h6">
              Monthly Transfers
            </Typography>

            <Typography variant="h4">
              18
            </Typography>
          </Paper>

        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>

          <Paper sx={{ padding: 3 }}>
            <Typography variant="h6">
              Beneficiaries
            </Typography>

            <Typography variant="h4">
              12
            </Typography>
          </Paper>

        </Grid>

      </Grid>

    </DashboardLayout>
  );
};

export default DashboardPage;