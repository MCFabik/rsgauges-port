$modDir = "."
$recipesDir = "$modDir\src\main\resources\data\rsgauges\recipe"
$lootDir = "$modDir\src\main\resources\data\rsgauges\loot_table\block"

# 1. Fix Recipes
if (Test-Path $recipesDir) {
    Get-ChildItem $recipesDir -Filter *.json | ForEach-Object {
        $content = Get-Content $_.FullName -Raw
        $modified = $false
        
        if ($content -match '"conditions"') {
            $content = $content -replace '"conditions"', '"neoforge:conditions"'
            $modified = $true
        }
        if ($content -match '"result"\s*:\s*\{\s*"item"') {
            $content = [regex]::Replace($content, '"result"\s*:\s*\{\s*"item"', '"result": { "id"')
            $modified = $true
        }
        if ($modified) {
            Set-Content $_.FullName -Value $content -Encoding UTF8
        }
    }
}

# 2. Generate Loot Tables for Blocks
if (-not (Test-Path $lootDir)) {
    New-Item -ItemType Directory -Path $lootDir | Out-Null
}

$modContentFile = "$modDir\src\main\java\wile\rsgauges\ModContent.java"
if (Test-Path $modContentFile) {
    $content = Get-Content $modContentFile -Raw
    $matches = [regex]::Matches($content, 'Registries\.addBlock\(\s*"([^"]+)"')
    foreach ($m in $matches) {
        $blockName = $m.Groups[1].Value
        $lootJson = @"
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1.0,
      "bonus_rolls": 0.0,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "rsgauges:$blockName"
        }
      ],
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ]
    }
  ]
}
"@
        $lootFile = "$lootDir\$blockName.json"
        Set-Content -Path $lootFile -Value $lootJson -Encoding UTF8
    }
}
