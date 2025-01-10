import path from "path";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"), // Shorter imports using '@' for the src directory
    },
  },
  optimizeDeps: {
    exclude: ["lucide-react"], // Ensures 'lucide-react' is not pre-bundled by Vite
  },
  server: {
    port: process.env.PORT ? parseInt(process.env.PORT) : 3000, // Use dynamic port from environment variable
    open: true, // Optional: Automatically open the browser
    host: "0.0.0.0", // Bind to all interfaces (important for deployment on cloud services)
  },
  build: {
    sourcemap: true, // Optional: Useful for debugging the production build
  },
});
