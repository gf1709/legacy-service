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
    # echo_ok($branch)
    echo_ok('current branch is ', $b)
    $searchString = '\bif\b'
    git checkout $b
    $grepResult = Get-ChildItem -Recurse -Include *.java | Select-String -Pattern  $searchString
    if ($grepResult) {
        "found----------------------------------" | Out-File -FilePath res.txt -Append -Encoding utf8
        $b | Out-File -FilePath res.txt -Append -Encoding utf8
        $grepResult | Out-File -FilePath res.txt -Append -Encoding utf8
        "sector end-----------------------------" | Out-File -FilePath res.txt -Append -Encoding utf8
        ""
    }
}
# Get-ChildItem -Recurse -Include *.java | Select-String -Pattern 'if'