from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler
from pathlib import Path

WEB_ROOT = Path(__file__).parent / "static"
HOST = ""
PORT = 8083


class DashboardHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(WEB_ROOT), **kwargs)

    def list_directory(self, path):
        self.send_error(403, "Directory listing is disabled")
        return None

    def do_GET(self):
        if self.path == "/static" or self.path.startswith("/static/"):
            self.send_error(404, "Not Found")
            return

        if self.path in ("", "/"):
            self.path = "/index.html"

        super().do_GET()


def run():
    server_address = (HOST, PORT)
    httpd = ThreadingHTTPServer(server_address, DashboardHandler)
    print(f"Dashboard available at http://localhost:{PORT}")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        httpd.server_close()


if __name__ == "__main__":
    run()

