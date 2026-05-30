import os
import json
import glob

# Paths
mod_dir = r"c:\Users\Fabia\Desktop\Java\rsgaugesport"
recipes_dir = os.path.join(mod_dir, "src", "main", "resources", "data", "rsgauges", "recipe")
loot_dir = os.path.join(mod_dir, "src", "main", "resources", "data", "rsgauges", "loot_table", "block")

# 1. Fix Recipes
for filename in glob.glob(os.path.join(recipes_dir, "*.json")):
    with open(filename, "r", encoding="utf-8") as f:
        try:
            data = json.load(f)
        except json.JSONDecodeError:
            continue
            
    modified = False
    
    # Fix neoforge:conditions
    if "conditions" in data:
        data["neoforge:conditions"] = data.pop("conditions")
        modified = True
        
    # Fix result format (item -> id)
    if "result" in data and isinstance(data["result"], dict):
        if "item" in data["result"]:
            data["result"]["id"] = data["result"].pop("item")
            modified = True
            
    if modified:
        with open(filename, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=2)

# 2. Generate Loot Tables for Blocks
os.makedirs(loot_dir, exist_ok=True)

# Read ModContent.java to find all registered blocks
modcontent_path = os.path.join(mod_dir, "src", "main", "java", "wile", "rsgauges", "ModContent.java")
if os.path.exists(modcontent_path):
    with open(modcontent_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    import re
    # Match: Registries.addBlock("block_name"
    matches = re.findall(r'Registries\.addBlock\(\s*"([^"]+)"', content)
    for block_name in matches:
        loot_table = {
            "type": "minecraft:block",
            "pools": [
                {
                    "rolls": 1,
                    "bonus_rolls": 0,
                    "entries": [
                        {
                            "type": "minecraft:item",
                            "name": f"rsgauges:{block_name}"
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
        
        loot_path = os.path.join(loot_dir, f"{block_name}.json")
        with open(loot_path, "w", encoding="utf-8") as f:
            json.dump(loot_table, f, indent=2)

print("Done fixing recipes and generating loot tables.")
