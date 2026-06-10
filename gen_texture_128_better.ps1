Add-Type -AssemblyName System.Drawing
$img = New-Object System.Drawing.Bitmap(128,128)
$gfx = [System.Drawing.Graphics]::FromImage($img)
$gfx.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$gfx.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$gfx.Clear([System.Drawing.Color]::Transparent)

$palette = @{
    '0' = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
    '1' = [System.Drawing.Color]::FromArgb(255, 30, 30, 30)      # dark grey outline
    '2' = [System.Drawing.Color]::FromArgb(255, 255, 255, 255)    # white highlight
    '3' = [System.Drawing.Color]::FromArgb(255, 0, 255, 255)      # bright cyan
    '4' = [System.Drawing.Color]::FromArgb(255, 0, 200, 200)      # medium cyan
    '5' = [System.Drawing.Color]::FromArgb(255, 0, 150, 150)      # dark cyan
    '6' = [System.Drawing.Color]::FromArgb(255, 170, 170, 170)    # grey metal
    '7' = [System.Drawing.Color]::FromArgb(255, 210, 210, 210)    # light grey handle
    '8' = [System.Drawing.Color]::FromArgb(255, 70, 70, 70)       # plunger rubber
}

$grid = @(
    "00000000000001111",
    "00000000000017771",
    "00000000001177100",
    "00000000017611000",
    "00000000116100000",
    "00000000122210000",
    "00000001288810000",
    "00000012338100000",
    "00000123331000000",
    "00001233410000000",
    "00012445100000000",
    "00111111000000000",
    "00166100000000000",
    "01610000000000000",
    "16100000000000000",
    "11000000000000000"
)

# Fix row lengths
$grid = @(
    "0000000000000111",
    "0000000000001771",
    "0000000000117710",
    "0000000001761100",
    "0000000011610000",
    "0000000012221000",
    "0000000128881000",
    "0000001233810000",
    "0000012333100000",
    "0000123341000000",
    "0001244510000000",
    "0011111100000000",
    "0016610000000000",
    "0161000000000000",
    "1610000000000000",
    "1100000000000000"
)


$scale = 8

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

$dir = "c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\item"
$path = Join-Path $dir "awesome_syringe.png"
$img.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
Write-Host "Saved improved 128x128 texture to $path"
