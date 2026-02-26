Set-Location -Path (Split-Path -Path $MyInvocation.MyCommand.Definition -Parent)

function echo_error {
    Write-Host $args[0] -BackgroundColor Red
}

function echo_ok {
    Write-Host $args[0] -BackgroundColor Blue
}

"start" | Out-File -FilePath res.txt -Encoding utf8

$branches = git branch -a | Where-Object { $_ -match 'remotes' -and $_ -notmatch 'HEAD' }
foreach ($branch in $branches) {
    $b = $branch -replace 'remotes/origin/', ''
    if ($b -eq 'master') {
        echo_error "ignore master !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        continue
    }
    git checkout $b
    $grepResult = Select-String -Path * -Pattern '\bmysql_user\b' -Exclude 'res.txt', $MyInvocation.MyCommand.Name -Recurse -Quiet
    if ($grepResult) {
        "found----------------------------------" | Out-File -FilePath res.txt -Append -Encoding utf8
        $b | Out-File -FilePath res.txt -Append -Encoding utf8
        Select-String -Path * -Pattern '\bmysql_user\b' -Exclude 'res.txt', $MyInvocation.MyCommand.Name -Recurse | Out-File -FilePath res.txt -Append -Encoding utf8
        "sector end-----------------------------" | Out-File -FilePath res.txt -Append -Encoding utf8
        ""
    }
}