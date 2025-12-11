#!/bin/sh
echo "Running TOTP cron at $(date -u)" >> /cron-output/last_code.txt
python3 /app/scripts/log_2fa_cron.py >> /cron-output/last_code.txt 2>&1
COPY docker/run_2fa_cron.sh /cron/run_2fa_cron.sh
RUN chmod +x /cron/run_2fa_cron.sh
