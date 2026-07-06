#!/bin/bash
# Script de arranque para la instancia EC2 (Ubuntu 22.04).
# Se ejecuta UNA sola vez, automaticamente, al crear la instancia.
# Deja Docker y Docker Compose listos; el despliegue de la app se hace
# despues, a mano, por SSH (ver deploy/INSTRUCCIONES.md).

set -e
apt-get update -y
apt-get install -y ca-certificates curl gnupg git

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

usermod -aG docker ubuntu
systemctl enable docker
systemctl start docker
