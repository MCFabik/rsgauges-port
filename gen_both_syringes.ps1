Add-Type -AssemblyName System.Drawing

$palette = @{
    '0' = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    '3' = [System.Drawing.Color]::FromArgb(255, 170, 255, 255)    # light cyan outline
    '6' = [System.Drawing.Color]::FromArgb(255, 170, 170, 170)    # grey needle/rod
    '7' = [System.Drawing.Color]::FromArgb(255, 85, 85, 85)       # dark grey handle/base
    '8' = [System.Drawing.Color]::FromArgb(255, 85, 85, 85)       # dark grey liquid
}

$grid1 = @(
    "0000000000000777",
    "0000000000007667",
    "0000000000077670",
    "0000000000336300",
    "0000000003888300",
    "0000000038888300",
    "0000000388883000",
    "0000003888830000",
    "0000038888300000",
    "0000388883000000",
    "0003888830000000",
    "0033883300000000",
    "0363300000000000",
    "7630000000000000",
    "7000000000000000",
    "0000000000000000"
)

$grid2 = @(
    "0000000000000777",
    "0000000000007667",
    "0000000000077670",
    "0000000000336300",
    "0000000003060300",
    "0000000030600300",
    "0000000306003000",
    "0000003060003000",
    "0000030600030000",
    "0000306000030000",
    "0003060000300000",
    "0033600330000000",
    "0363300000000000",
    "7630000000000000",
    "7000000000000000",
    "0000000000000000"
)

$scale = 8
$dir = "c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\item"
if (!(Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }

function DrawTexture($grid, $name) {
    $img = New-Object System.Drawing.Bitmap(128,128)
    $gfx = [System.Drawing.Graphics]::FromImage($img)
    $gfx.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
    $gfx.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    $gfx.Clear([System.Drawing.Color]::Transparent)

    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            if ($grid[$y][$x] -ne '0') {
                $color = $palette[$grid[$y][$x].ToString()]
                $brush = New-Object System.Drawing.SolidBrush($color)
                $gfx.FillRectangle($brush, $x * $scale, $y * $scale, $scale, $scale)
                $brush.Dispose()
            }
        }
    }
    $gfx.Dispose()
    $path = Join-Path $dir $name
    $img.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Host "Saved texture to $path"
}

DrawTexture $grid1 "awesome_syringe.png"
DrawTexture $grid2 "empty_syringe.png"
