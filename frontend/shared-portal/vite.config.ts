import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from "path";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
  alias: {
    "@": path.resolve(__dirname, "./src"),

    "@api": path.resolve(
      __dirname,
      "./src/api"
    ),

    "@components": path.resolve(
      __dirname,
      "./src/components"
    ),

    "@features": path.resolve(
      __dirname,
      "./src/features"
    ),

    "@layouts": path.resolve(
      __dirname,
      "./src/layouts"
    ),

    "@routes": path.resolve(
      __dirname,
      "./src/routes"
    ),

    "@store": path.resolve(
      __dirname,
      "./src/store"
    ),

    "@utils": path.resolve(
      __dirname,
      "./src/utils"
    ),
  },
},
})
