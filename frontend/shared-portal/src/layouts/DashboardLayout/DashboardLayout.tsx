import { Box } from "@mui/material";

import Sidebar from "../Sidebar/Sidebar";
import Header from "../Header/Header";

interface Props {
  children: React.ReactNode;
}

const DashboardLayout = ({ children }: Props) => {

  return (
    <Box sx={{ display: "flex" }}>

      <Sidebar />

      <Box sx={{ flexGrow: 1 }}>

        <Header />

        <Box sx={{ padding: 3 }}>
          {children}
        </Box>

      </Box>

    </Box>
  );
};

export default DashboardLayout;