import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/rest/api/v1': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})