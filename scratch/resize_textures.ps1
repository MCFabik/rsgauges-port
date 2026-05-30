Add-Type -AssemblyName System.Drawing

$srcDir = "C:\Users\Fabia\.gemini\antigravity-ide\brain\3729a6c1-9449-4fd2-ba09-e0409c9a27c7"
$destBlockDir = "c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\block"
$destItemDir = "c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\item"

# Ensure directories exist
New-Item -ItemType Directory -Force -Path $destBlockDir | Out-Null
New-Item -ItemType Directory -Force -Path $destItemDir | Out-Null

$textures = @{
    "transport_terminal_1779993450452.png" = Join-Path $destBlockDir "transport_terminal.png"
    "terminal_side_1779994352511.png"      = Join-Path $destBlockDir "transport_terminal_side.png"
    "terminal_top_1779994367563.png"       = Join-Path $destBlockDir "transport_terminal_top.png"
    "blank_chip_1779993463336.png"         = Join-Path $destItemDir "blank_transport_chip.png"
    "encoded_chip_1779993483566.png"       = Join-Path $destItemDir "encoded_transport_chip.png"
}

Write-Host "Resizing and copying textures to 256x256 using PowerShell .NET APIs..."

foreach ($srcName in $textures.Keys) {
    $srcPath = Join-Path $srcDir $srcName
    $destPath = $textures[$srcName]
    
    if (Test-Path $srcPath) {
        try {
            $img = [System.Drawing.Image]::FromFile($srcPath)
            # Resize to 256x256 as requested by the user
            $bmp = New-Object System.Drawing.Bitmap 256, 256
            $graph = [System.Drawing.Graphics]::FromImage($bmp)
            
            # Use high quality interpolation
            $graph.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
            $graph.DrawImage($img, 0, 0, 256, 256)
            
            # Save to target mod folder
            $bmp.Save($destPath, [System.Drawing.Imaging.ImageFormat]::Png)
            
            # Save a simplified name copy in the artifact dir for markdown rendering
            $simpleName = $srcName -replace '_\d+\.png$', '.png'
            $simpleArtifactPath = Join-Path $srcDir $simpleName
            $bmp.Save($simpleArtifactPath, [System.Drawing.Imaging.ImageFormat]::Png)
            
            # Dispose resources
            $graph.Dispose()
            $bmp.Dispose()
            $img.Dispose()
            
            Write-Host "Successfully processed $srcName -> $destPath (and $simpleArtifactPath) as 256x256"
        }
        catch {
            Write-Error "Error processing ${srcName}: $_"
        }
    } else {
        Write-Warning "Source file not found: $srcPath"
    }
}
