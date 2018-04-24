#include <stdio.h>

void main(int argc, char *argv[])
{
	printf("HTTP/1.1 200 OK\r\n");
	printf("Content-Type:text/html\r\n\n");
	printf("<html><head><title>ghBertuzzo Server</title></head><body><h1>/files ghBertuzzo Server</h1>");
	printf("Aqui a mensagem que voce queria: %s\n",argv[1]);
	printf("</body></html>");
}