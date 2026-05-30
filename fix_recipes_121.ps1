$modDir = "c:\Users\Fabia\Desktop\Java\rsgaugesport"
$recipesDir = "$modDir\src\main\resources\data\rsgauges\recipes"
$recipeDir = "$modDir\src\main\resources\data\rsgauges\recipe"

if (Test-Path $recipesDir) {
    Rename-Item -Path $recipesDir -NewName "recipe"
}

if (Test-Path $recipeDir) {
    Get-ChildItem -Path $recipeDir -Filter *.json -Recurse | ForEach-Object {
        $content = Get-Content $_.FullName -Raw
        $modified = $false
        
        if ($content -match '"conditions"\s*:') {
            $content = $content -replace '"conditions"\s*:', '"neoforge:conditions":'
            $modified = $true
        }
        
        if ($content -match '"result"\s*:\s*\{\s*"item"') {
            $content = [regex]::Replace($content, '"result"\s*:\s*\{\s*"item"', '"result": { "id"')
            $modified = $true
        }

        if ($content -match '\{\s*"item"\s*:\s*"([^"]+)"\s*\}') {
            $content = [regex]::Replace($content, '\{\s*"item"\s*:\s*"([^"]+)"\s*\}', '"$1"')
            $modified = $true
        }

        if ($content -match '\{\s*"tag"\s*:\s*"([^"]+)"\s*\}') {
            $content = [regex]::Replace($content, '\{\s*"tag"\s*:\s*"([^"]+)"\s*\}', '"#$1"')
            $modified = $true
        }

        if ($modified) {
            Set-Content $_.FullName -Value $content -Encoding UTF8
        }
    }
    Write-Host "Recipes fixed successfully."
} else {
    Write-Host "Recipe directory not found!"
}
