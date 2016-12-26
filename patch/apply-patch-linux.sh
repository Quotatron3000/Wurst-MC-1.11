#This basically converts the DOS commands into linux compatible commands

cd ../mc
git checkout master
git apply --ignore-space-change --ignore-whitespace ../patch/minecraft.patch
cp -rf ../mc ../tmp
git reset --hard
git checkout modded
cp -rf ../tmp ../mc
rm -rf ../tmp