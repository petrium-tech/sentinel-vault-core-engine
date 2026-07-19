#!/bin/bash

echo "[1/2] 🗑️ Destruindo containers e volumes antigos..."
docker compose down -v

echo "[2/2] 🚀 Construindo imagem e subindo ecossistema Sentinel Vault Core..."
docker compose up --build -d

echo "--------------------------------------------------------"
echo "✨ Fortaleza Sentinel Vault Core Engine inicializada!"
echo "📡 Acompanhando os logs da aplicação abaixo (Ctrl+C para sair):"
echo "--------------------------------------------------------"

# Exibe os logs da aplicação em tempo real para você acompanhar o Flyway e o Boot
docker compose logs -f sentinel-app