KSRC = "git://github.com/Nuvoton-Israel/linux;protocol=https;branch=${KBRANCH}"
KBRANCH = "NPCM-6.1-OpenBMC"
LINUX_VERSION = "6.1.12"
SRCREV = "852afa3135f4b0e5f1c8dbe0152a94ad660cdf31"

require linux-nuvoton.inc

SRC_URI:append:nuvoton = " file://0017-drivers-i2c-workaround-for-i2c-slave-behavior.patch"
SRC_URI:append:nuvoton = " file://enable-emc.cfg"

# New Arch VDMX/VDMA driver
# SRC_URI:append:nuvoton = " file://2222-driver-misc-add-nuvoton-vdmx-vdma-driver.patch"

# SRC_URI:append:nuvoton = " file://0004-driver-ncsi-replace-del-timer-sync.patch"
# SRC_URI:append:nuvoton = " file://0015-driver-misc-nuvoton-vdm-support-openbmc-libmctp.patch"
