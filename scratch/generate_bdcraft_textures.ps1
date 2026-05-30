Add-Type -AssemblyName System.Drawing

# Paths
$standaloneDir = "c:\Users\Fabia\Desktop\Java\textures\blocks"
$modDir = "c:\Users\Fabia\Desktop\Java\rsgaugesport\src\main\resources\assets\rsgauges\textures\block"

# Create directories if they don't exist
If (!(Test-Path $standaloneDir)) { New-Item -ItemType Directory -Force -Path $standaloneDir | Out-Null }
If (!(Test-Path $modDir)) { New-Item -ItemType Directory -Force -Path $modDir | Out-Null }

# =========================================================================
# GRAPHICS DRAWING HELPERS (Simplified Pixel-Art Style)
# =========================================================================

# Helper to fill with clean pixel-art metal texture (subtle, soft grain)
function Add-PixelMetal {
    param (
        [System.Drawing.Bitmap]$bmp,
        [int]$startX, [int]$startY, [int]$width, [int]$height,
        [int]$baseGray
    )
    $rand = New-Object System.Random
    for ($y = $startY; $y -lt ($startY + $height); $y++) {
        for ($x = $startX; $x -lt ($startX + $width); $x++) {
            $grain = $rand.Next(-1, 2) # Very subtle grain [-1, 1]
            $val = $baseGray + $grain
            $val = [Math]::Max(0, [Math]::Min(255, $val))
            $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, $val, $val, $val))
        }
    }
}

# Helper to apply clean pixel-art bevel (2-stage stepping border)
function Apply-PixelBevel {
    param (
        [System.Drawing.Bitmap]$bmp,
        [int]$x, [int]$y, [int]$w, [int]$h,
        [int]$borderSize,
        [float]$maxStrength
    )
    $halfSize = [Math]::Floor($borderSize / 2)
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $distLeft = $px - $x
            $distRight = ($x + $w - 1) - $px
            $distTop = $py - $y
            $distBottom = ($y + $h - 1) - $py
            
            $minDist = [Math]::Min([Math]::Min($distLeft, $distRight), [Math]::Min($distTop, $distBottom))
            
            if ($minDist -lt $borderSize) {
                # 2-stage bevel stepping
                $strength = if ($minDist -lt $halfSize) { $maxStrength } else { $maxStrength * 0.5 }
                
                $diagonal = ($px - $x) / $w + ($py - $y) / $h
                $p = $bmp.GetPixel($px, $py)
                
                if ($diagonal -lt 1.0) {
                    # Top-Left Highlight
                    $r = [int]($p.R + (255 - $p.R) * $strength)
                    $g = [int]($p.G + (255 - $p.G) * $strength)
                    $b = [int]($p.B + (255 - $p.B) * $strength)
                } else {
                    # Bottom-Right Shadow
                    $r = [int]($p.R * (1.0 - $strength))
                    $g = [int]($p.G * (1.0 - $strength))
                    $b = [int]($p.B * (1.0 - $strength))
                }
                
                $r = [Math]::Max(0, [Math]::Min(255, $r))
                $g = [Math]::Max(0, [Math]::Min(255, $g))
                $b = [Math]::Max(0, [Math]::Min(255, $b))
                $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, $r, $g, $b))
            }
        }
    }
}

# Helper to draw a shaded metal rivet (simplified pixel art dome)
function Draw-Rivet {
    param ($bmp, $cx, $cy, $radius)
    $baseGray = 86
    $highlightColor = [System.Drawing.Color]::FromArgb(255, 130, 130, 134)
    $shadowColor = [System.Drawing.Color]::FromArgb(255, 58, 58, 60)
    
    $recessRadius = $radius + 2
    for ($y = $cy - $recessRadius; $y -le $cy + $recessRadius; $y++) {
        for ($x = $cx - $recessRadius; $x -le $cx + $recessRadius; $x++) {
            $dx = $x - $cx
            $dy = $y - $cy
            $dist = [Math]::Sqrt($dx*$dx + $dy*$dy)
            
            if ($dist -le $recessRadius) {
                if ($dist -gt $radius) {
                    # Recess border shadow
                    $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 31, 31, 33))
                } else {
                    # Flat pixel-art dome shading
                    $diagonal = $dx + $dy
                    if ($diagonal -lt 0) {
                        $bmp.SetPixel($x, $y, $highlightColor)
                    } else {
                        $bmp.SetPixel($x, $y, $shadowColor)
                    }
                }
            }
        }
    }
    # Specular spot
    $bmp.SetPixel(($cx - 1), ($cy - 1), [System.Drawing.Color]::White)
}

# Helper to draw flat warning checkers (mustard yellow & off-black)
function Draw-WarningCheckers {
    param ($bmp, $startY, $height)
    $yCheckYellow = [System.Drawing.Color]::FromArgb(255, 181, 128, 16)
    $yCheckBlack = [System.Drawing.Color]::FromArgb(255, 29, 29, 31)
    
    for ($x = 0; $x -lt 128; $x++) {
        $col = [Math]::Floor($x / 8)
        for ($y = $startY; $y -lt ($startY + $height); $y++) {
            $row = [Math]::Floor(($y - $startY) / 8)
            if (($col + $row) % 2 -eq 0) {
                $bmp.SetPixel($x, $y, $yCheckYellow)
            } else {
                $bmp.SetPixel($x, $y, $yCheckBlack)
            }
        }
    }
    # Clean outline
    for ($y = $startY; $y -lt ($startY + $height); $y += 8) {
        for ($x = 0; $x -lt 128; $x++) { $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 20, 20, 22)) }
    }
    for ($x = 0; $x -lt 128; $x += 8) {
        for ($y = $startY; $y -lt ($startY + $height); $y++) { $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 20, 20, 22)) }
    }
    Apply-PixelBevel $bmp 0 $startY 128 $height 4 0.3
}

# Helper to draw a simplified CRT green screen
function Draw-CRTScreen {
    param ($bmp, $x, $y, $w, $h)
    $screenBg = [System.Drawing.Color]::FromArgb(255, 8, 24, 10)
    $ringColor = [System.Drawing.Color]::FromArgb(255, 48, 176, 80)
    
    # Fill screen background
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $bmp.SetPixel($px, $py, $screenBg)
        }
    }
    
    # Draw simple concentric rings
    $cx = $x + $w / 2
    $cy = $y + $h / 2
    for ($r = 10; $r -le 45; $r += 10) {
        for ($py = $y; $py -lt ($y + $h); $py++) {
            for ($px = $x; $px -lt ($x + $w); $px++) {
                $dx = $px - $cx
                $dy = $py - $cy
                $dist = [Math]::Sqrt($dx*$dx + $dy*$dy)
                if ([Math]::Abs($dist - $r) -lt 1.5) {
                    $bmp.SetPixel($px, $py, $ringColor)
                }
            }
        }
    }
    
    # Simple telemetry bars
    for ($py = $y + 6; $py -lt ($y + $h - 6); $py++) {
        # Green bar on left
        if ($py % 4 -ne 0) {
            for ($px = $x + 4; $px -le $x + 6; $px++) { $bmp.SetPixel($px, $py, $ringColor) }
        }
        # Cyan bar on right
        if ($py % 3 -ne 0) {
            for ($px = $x + $w - 7; $px -le $x + $w - 5; $px++) { 
                $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 0, 160, 192)) 
            }
        }
    }
    
    # Scanlines (15% darker horizontal lines every 3 pixels)
    for ($py = $y; $py -lt ($y + $h); $py += 3) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $p = $bmp.GetPixel($px, $py)
            $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, [int]($p.R * 0.8), [int]($p.G * 0.8), [int]($p.B * 0.8)))
        }
    }
    
    # Clean diagonal glare sweep
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $diag = ($px - $x) + ($py - $y)
            if ([Math]::Abs($diag - 40) -lt 8) {
                $p = $bmp.GetPixel($px, $py)
                $r = [Math]::Min(255, $p.R + 24)
                $g = [Math]::Min(255, $p.G + 24)
                $b = [Math]::Min(255, $p.B + 24)
                $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, $r, $g, $b))
            }
        }
    }
}

# Helper to draw mechanical 3D keycaps
function Draw-3DKey {
    param ($bmp, $x, $y, $w, $h)
    $hColor = [System.Drawing.Color]::FromArgb(255, 80, 80, 85)     # Highlight
    $sColor = [System.Drawing.Color]::FromArgb(255, 20, 20, 22)     # Shadow
    $cColor = [System.Drawing.Color]::FromArgb(255, 32, 32, 35)     # Cap face
    
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            if ($px -eq $x -or $py -eq $y) {
                $bmp.SetPixel($px, $py, $hColor)
            } elseif ($px -eq ($x + $w - 1) -or $py -eq ($y + $h - 1)) {
                $bmp.SetPixel($px, $py, $sColor)
            } else {
                $bmp.SetPixel($px, $py, $cColor)
            }
        }
    }
}

# Helper to draw a flat pixel-art push button
function Draw-FlatButton {
    param ($bmp, $x, $y, $w, $h, $color, $glowColor)
    # Recessed shadow ring
    for ($py = $y - 1; $py -le $y + $h; $py++) {
        for ($px = $x - 1; $px -le $x + $w; $px++) {
            if ($px -eq $x - 1 -or $px -eq $x + $w -or $py -eq $y - 1 -or $py -eq $y + $h) {
                $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 24, 24, 26))
            }
        }
    }
    # Button cap fill
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $diagonal = ($px - $x) + ($py - $y)
            if ($diagonal -lt ($w)) {
                $bmp.SetPixel($px, $py, $glowColor)
            } else {
                $bmp.SetPixel($px, $py, $color)
            }
        }
    }
    # Highlight dot
    $bmp.SetPixel(($x + 2), ($y + 2), [System.Drawing.Color]::White)
}

# Helper to draw a simple glowing pad grid
function Draw-FlatGrid {
    param ($bmp, $x, $y, $w, $h)
    $bgCol = [System.Drawing.Color]::FromArgb(255, 22, 6, 32)
    $gridCol = [System.Drawing.Color]::FromArgb(255, 180, 0, 230)
    
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $bmp.SetPixel($px, $py, $bgCol)
        }
    }
    
    # Grid lines (2-pixels wide)
    for ($px = $x + 12; $px -lt ($x + $w - 4); $px += 12) {
        for ($py = $y; $py -lt ($y + $h); $py++) {
            $bmp.SetPixel($px, $py, $gridCol)
            $bmp.SetPixel(($px + 1), $py, $gridCol)
        }
    }
    for ($py = $y + 8; $py -lt ($y + $h - 4); $py += 8) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $bmp.SetPixel($px, $py, $gridCol)
            $bmp.SetPixel($px, ($py + 1), $gridCol)
        }
    }
    
    # Center glow circles (simplified)
    $cx = $x + $w / 2
    $cy = $y + $h / 2
    for ($py = $cy - 12; $py -le $cy + 12; $py++) {
        for ($px = $cx - 12; $px -le $cx + 12; $px++) {
            $dx = $px - $cx
            $dy = $py - $cy
            $dist = [Math]::Sqrt($dx*$dx + $dy*$dy)
            if ($dist -le 12) {
                if ($dist -le 4) {
                    $bmp.SetPixel($px, $py, [System.Drawing.Color]::White)
                } else {
                    $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 220, 0, 180))
                }
            }
        }
    }
}

# Helper to draw vertical coil panels with simple horizontal glowing rings
function Draw-FlatCoil {
    param ($bmp, $x, $y, $w, $h)
    for ($py = $y; $py -lt ($y + $h); $py++) {
        for ($px = $x; $px -lt ($x + $w); $px++) {
            $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 30, 30, 36))
        }
    }
    
    # Glowing rings
    $yPositions = @(24, 36, 48, 60)
    foreach ($cy in $yPositions) {
        # Blue border ring
        for ($py = $cy - 2; $py -le $cy + 2; $py++) {
            for ($px = $x + 1; $px -lt ($x + $w - 1); $px++) {
                $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 0, 100, 180))
            }
        }
        # Bright cyan core
        for ($py = $cy - 1; $py -le $cy; $py++) {
            for ($px = $x + 2; $px -lt ($x + $w - 2); $px++) {
                $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 0, 210, 240))
            }
        }
    }
}


# =========================================================================
# 1. CASING SIDE TEXTURE (transport_terminal_side.png) -> #sides
# =========================================================================
$bmpSide = New-Object System.Drawing.Bitmap 128, 128
Add-PixelMetal $bmpSide 0 0 128 128 86
Apply-PixelBevel $bmpSide 0 0 128 128 8 0.35

# Recessed side panel groove
# Position: (20, 32) to (108, 96) (size: 88x64)
# Fill with darker gray metal (60 base)
Add-PixelMetal $bmpSide 20 32 88 64 60
# Recessed inner shadow border
for ($py = 32; $py -lt 96; $py++) {
    for ($px = 20; $px -lt 108; $px++) {
        $distX = [Math]::Min($px - 20, 107 - $px)
        $distY = [Math]::Min($py - 32, 95 - $py)
        $minDist = [Math]::Min($distX, $distY)
        if ($minDist -lt 4) {
            $f = 1.0 - ($minDist / 4)
            $p = $bmpSide.GetPixel($px, $py)
            $r = [int]($p.R * (1.0 - $f * 0.35))
            $g = [int]($p.G * (1.0 - $f * 0.35))
            $b = [int]($p.B * (1.0 - $f * 0.35))
            $bmpSide.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, $r, $g, $b))
        }
    }
}

# Corner Rivets
Draw-Rivet $bmpSide 24 24 4
Draw-Rivet $bmpSide 104 24 4
Draw-Rivet $bmpSide 24 92 4
Draw-Rivet $bmpSide 104 92 4


# =========================================================================
# 2. CONSOLE/SCREEN TEXTURE (transport_terminal.png) -> #top
# =========================================================================
$bmpConsole = New-Object System.Drawing.Bitmap 128, 128
Add-PixelMetal $bmpConsole 0 0 128 128 86
Apply-PixelBevel $bmpConsole 0 0 128 128 8 0.35

Draw-Rivet $bmpConsole 24 24 4
Draw-Rivet $bmpConsole 104 24 4

# Screen Frame Bezel
Apply-PixelBevel $bmpConsole 14 6 100 60 4 0.30

# CRT Screen inside bezel (Position: 16, 8 to 112, 64)
Draw-CRTScreen $bmpConsole 16 8 96 56

# Horizontal separator bar between screen and console
Add-PixelMetal $bmpConsole 8 72 112 8 76
Apply-PixelBevel $bmpConsole 8 72 112 8 2 0.25

# Keyboard console plate
Add-PixelMetal $bmpConsole 8 80 112 40 52
Apply-PixelBevel $bmpConsole 8 80 112 40 4 0.30

# Keyboard Keys
for ($ky = 92; $ky -le 108; $ky += 8) {
    for ($kx = 16; $kx -le 64; $kx += 8) {
        Draw-3DKey $bmpConsole $kx $ky 6 6
    }
}

# Glowing Green Button
Draw-FlatButton $bmpConsole 72 80 16 16 ([System.Drawing.Color]::FromArgb(255, 42, 140, 76)) ([System.Drawing.Color]::FromArgb(255, 76, 212, 126))

# Glowing Red Button
Draw-FlatButton $bmpConsole 96 80 16 16 ([System.Drawing.Color]::FromArgb(255, 156, 42, 48)) ([System.Drawing.Color]::FromArgb(255, 224, 90, 96))

# Card Reader Slot
Apply-PixelBevel $bmpConsole 16 80 40 6 2 0.25
for ($py = 82; $py -le 83; $py++) {
    for ($px = 20; $px -le 52; $px++) {
        $bmpConsole.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255, 12, 12, 14))
    }
}

# Status LED
$bmpConsole.SetPixel(60, 81, [System.Drawing.Color]::FromArgb(255, 24, 24, 26))
$bmpConsole.SetPixel(59, 82, [System.Drawing.Color]::FromArgb(255, 24, 24, 26))
$bmpConsole.SetPixel(61, 82, [System.Drawing.Color]::FromArgb(255, 24, 24, 26))
$bmpConsole.SetPixel(60, 83, [System.Drawing.Color]::FromArgb(255, 24, 24, 26))
$bmpConsole.SetPixel(60, 82, [System.Drawing.Color]::FromArgb(255, 0, 180, 200))


# =========================================================================
# 3. BASE CHECKERBOARD TEXTURE (transport_terminal_base.png) -> #base
# =========================================================================
$bmpBase = New-Object System.Drawing.Bitmap 128, 128
Draw-WarningCheckers $bmpBase 0 128


# =========================================================================
# 4. TELEPORTER PAD GRID TEXTURE (transport_terminal_pad.png) -> #pad
# =========================================================================
$bmpPad = New-Object System.Drawing.Bitmap 128, 128
Add-PixelMetal $bmpPad 0 0 128 128 54
Apply-PixelBevel $bmpPad 0 0 128 128 8 0.35
Apply-PixelBevel $bmpPad 16 8 96 48 4 0.30
Draw-FlatGrid $bmpPad 16 8 96 48


# =========================================================================
# 5. COIL TEXTURE (transport_terminal_coil.png) -> #coil
# =========================================================================
$bmpCoil = New-Object System.Drawing.Bitmap 128, 128
Add-PixelMetal $bmpCoil 0 0 128 128 86
Apply-PixelBevel $bmpCoil 0 0 128 128 8 0.35
Draw-FlatCoil $bmpCoil 32 16 24 56
Draw-FlatCoil $bmpCoil 72 16 24 56


# =========================================================================
# 6. BOTTOM TEXTURE (transport_terminal_bottom.png) -> #bottom
# =========================================================================
$bmpBottom = New-Object System.Drawing.Bitmap 128, 128
Add-PixelMetal $bmpBottom 0 0 128 128 55
Apply-PixelBevel $bmpBottom 0 0 128 128 8 0.3


# =========================================================================
# SAVE ALL TEXTURES TO TARGET DIRECTORIES
# =========================================================================
$textures = @{
    "transport_terminal_side.png"   = $bmpSide
    "transport_terminal.png"        = $bmpConsole
    "transport_terminal_base.png"   = $bmpBase
    "transport_terminal_pad.png"    = $bmpPad
    "transport_terminal_coil.png"   = $bmpCoil
    "transport_terminal_bottom.png" = $bmpBottom
}

foreach ($filename in $textures.Keys) {
    $bmp = $textures[$filename]
    $bmp.Save((Join-Path $standaloneDir $filename), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save((Join-Path $modDir $filename), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

Write-Output "All 5 simplified 128x128 textures generated successfully with GDI+ pixel-art engine!"
