# Script para matar processo na porta 8080
Write-Host "Procurando processo na porta 8080..." -ForegroundColor Yellow

$port = 8080
$process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique

if ($process) {
    Write-Host "Processo encontrado: PID $process" -ForegroundColor Red
    $processInfo = Get-Process -Id $process -ErrorAction SilentlyContinue
    if ($processInfo) {
        Write-Host "Nome do processo: $($processInfo.ProcessName)" -ForegroundColor Cyan
        Write-Host "Encerrando processo..." -ForegroundColor Yellow
        Stop-Process -Id $process -Force
        Write-Host "Processo encerrado com sucesso!" -ForegroundColor Green
    }
} else {
    Write-Host "Nenhum processo encontrado na porta $port" -ForegroundColor Green
}

Write-Host "`nPressione qualquer tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")




