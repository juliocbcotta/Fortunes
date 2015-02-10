mkdir -p drawable-ldpi
mkdir -p drawable-mdpi
mkdir -p drawable-hdpi
mkdir -p drawable-xhdpi
mkdir -p drawable-xxhdpi
mkdir -p drawable-xxxhdpi

for target in `ls drawable* -d`;
do
    cp ic_*.png $target/
done

cd drawable-ldpi
mogrify -resize 36 *.png
cd ..

cd drawable-mdpi
mogrify -resize 48 *.png
cd ..

cd drawable-hdpi
mogrify -resize 72 *.png
cd ..


cd drawable-xhdpi
mogrify -resize 96 *.png
cd ..

cd drawable-xxhdpi
mogrify -resize 144 *.png
cd ..

cd drawable-xxxhdpi
mogrify -resize 192 *.png
cd ..

 for i in `ls drawable* -d`;
 do
 	cp $i/* /home/julio/AndroidStudioProjects/Fortunes/app/src/main/res/$i/;
 done

rm -rf drawable-ldpi
rm -rf drawable-mdpi
rm -rf drawable-hdpi
rm -rf drawable-xhdpi
rm -rf drawable-xxhdpi
rm -rf drawable-xxxhdpi
