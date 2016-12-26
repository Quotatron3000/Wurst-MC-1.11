#This basically converts the DOS commands into linux compatible commands
cd ../mc
git init
git add .
git commit -a -m "Initial commit"
git checkout -b modded
git checkout master
git apply --ignore-space-change --ignore-whitespace ../patch/minecraft.patch
mkdir ../tmp
\cp -rf ../mc ../tmp
git reset --hard
git checkout modded
\cp -rf ../tmp ../mc
rm -rf ../tmp
git commit -a -m "Add Wurst changes"