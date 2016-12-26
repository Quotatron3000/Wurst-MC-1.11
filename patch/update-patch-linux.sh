#This basically converts the DOS commands into linux compatible commands

cd ../mc
git checkout master
git checkout -b tmp
git merge --squash modded
git commit -a -m "mod"
git format-patch master --ignore-space-change
git checkout modded
git branch -D tmp
rsync --remove-source-files 0001-mod.patch ../patch/minecraft.patch