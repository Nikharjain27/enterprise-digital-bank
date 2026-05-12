import {
  Card,
  CardContent,
  Typography,
} from "@mui/material";

interface Props {
  title: string;
  value: string | number;
  subtitle?: string;
}

const SummaryCard = ({
  title,
  value,
  subtitle,
}: Props) => {

  return (

    <Card
      sx={{
        borderRadius: 4,
        height: "100%",
      }}
    >

      <CardContent>

        <Typography
          variant="body2"
          color="text.secondary"
        >
          {title}
        </Typography>

        <Typography
          variant="h4"
          sx={{
            mt: 1,
            fontWeight: 700,
          }}
        >
          {value}
        </Typography>

        {subtitle && (

          <Typography
            variant="body2"
            sx={{ mt: 1 }}
          >
            {subtitle}
          </Typography>

        )}

      </CardContent>

    </Card>
  );
};

export default SummaryCard;