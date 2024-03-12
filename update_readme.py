import os

def generate_directory_tree(root_dir='.', indent='', ignored_dirs=None):
    if ignored_dirs is None:
        ignored_dirs = ['.git', '.gradle', '.gitignore', 'out', '.idea']

    tree_structure = ""
    items = sorted(os.listdir(root_dir))
    
    for i, item in enumerate(items):
        item_path = os.path.join(root_dir, item)
        is_last = i == len(items) - 1
        
        if os.path.isdir(item_path) and item not in ignored_dirs:
            tree_structure += indent + "├── " + item + "\n"
            if is_last:
                next_indent = indent + "    "
            else:
                next_indent = indent + "│   "
            tree_structure += generate_directory_tree(item_path, next_indent, ignored_dirs)
        elif item not in ignored_dirs:
            tree_structure += indent + "└── " + item + "\n"
    
    return tree_structure

def write_to_readme(tree_structure):
    with open("README.md", "w", encoding="utf-8") as readme_file:
        readme_file.write("# Directory Tree Structure\n\n")
        readme_file.write("```\n")
        readme_file.write(tree_structure)
        readme_file.write("```\n")

if __name__ == "__main__":
    root_directory = "."
    directory_tree = generate_directory_tree(root_directory)
    write_to_readme(directory_tree)
    print("README.md updated successfully.")