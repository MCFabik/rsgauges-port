Add-Type -AssemblyName System.Drawing
$img = New-Object System.Drawing.Bitmap(16,16)

$palette = @{
    '0' = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    '1' = [System.Drawing.Color]::FromArgb(255, 34, 34, 34)
    '2' = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)
    '3' = [System.Drawing.Color]::FromArgb(255, 136, 255, 255)
    '4' = [System.Drawing.Color]::FromArgb(255, 0, 170, 170)
    '5' = [System.Drawing.Color]::FromArgb(255, 170, 170, 170)
    '6' = [System.Drawing.Color]::FromArgb(255, 210, 210, 210)
    '7' = [System.Drawing.Color]::FromArgb(255, 85, 85, 85)
}

$grid = @(
    "0000000000000111",
    "0000000000001661",
    "0000000000011510",
    "0000000000161000",
    "0000000001111000",
    "0000000016271000",
    "0000000162371000",
    "0000001623310000",
    "0000016233410000",
    "0000162334100000",
    "0000111111000000",
    "0001510000000000",
    "0015100000000000",
    "0151000000000000",
    "0110000000000000",
    "0000000000000000"
)

for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        $color = $palette[$grid[$y][$x].ToString()]
        $img.SetPixel($x, $y, $color)
    }
}

$dir = "c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\item"
if (!(Test-Path $dir)) {
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
}
$path = Join-Path $dir "awesome_syringe.png"
$img.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
Write-Host "Saved texture to $path"
