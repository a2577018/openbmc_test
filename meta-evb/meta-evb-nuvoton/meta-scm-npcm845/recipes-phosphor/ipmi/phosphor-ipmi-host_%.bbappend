FILESEXTRAPATHS:append:scm-npcm845 := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append:scm-npcm845 = " -Di2c-whitelist-check=disabled"

SRC_URI:append:scm-npcm845 = " file://0001-Add-set-BIOS-version-support.patch"
SRC_URI:append:scm-npcm845 = " file://0002-Add-SEL-add-command.patch"
SRC_URI:append:scm-npcm845 = " file://0003-Add-sensor-type-command.patch"
SRC_URI:append:scm-npcm845 = " file://0004-ipmi-warm-reset-command.patch"
SRC_URI:append:scm-npcm845 = " file://0005-get-system-guid-command.patch"
SRC_URI:append:scm-npcm845 = " file://0006-Add-SEL-time-set-command.patch"
SRC_URI:append:scm-npcm845 = " file://0007-Force-self-test-OK.patch"
SRC_URI:append:scm-npcm845 = " file://0008-Set-is-from-system-interface-return-false.patch"
SRC_URI:append:scm-npcm845 = " file://0009-Add-reset-SEL.patch"
SRC_URI:append:scm-npcm845 = " file://0010-Add-session-RemoteMACAddress-support.patch"
SRC_URI:append:scm-npcm845 = " file://0011-add-server-type-and-oem-id-to-meet-MS-spec.patch"
SRC_URI:append:scm-npcm845 = " file://0012-fix-percentage-type-show.patch"
SRC_URI:append:scm-npcm845 = " file://0013-sensor-reading-optional-zero.patch"
SRC_URI:append:scm-npcm845 = " file://0014-add-sensor-reading-factory-support.patch"
SRC_URI:append:scm-npcm845 = " file://0015-add-oem-sel-support.patch"
SRC_URI:append:scm-npcm845 = " file://0016-update-chassishandler-from-intel-oem-ipmi.patch"
SRC_URI:append:scm-npcm845 = " file://0017-save-no-supported-boot-options.patch"
SRC_URI:append:scm-npcm845 = " file://0018-set-channel-security-keys.patch"
SRC_URI:append:scm-npcm845 = " file://0019-Use-BMC-state-is-POR-to-get-ACfail-status.patch"

PACKAGECONFIG:append:scm-npcm845 = " dynamic-sensors"

# avoid build error after remove ipmi-fru
WHITELIST_CONF:scm-npcm845 = "${S}/host-ipmid-whitelist.conf"

# support ipmi warm reset
FILES:${PN}:append:scm-npcm845 = " ${systemd_system_unitdir}/phosphor-ipmi-host.service.d/ipmi-warm-reset.conf"
SRC_URI:append:scm-npcm845 = " file://ipmi-warm-reset.conf"
SYSTEMD_SERVICE:${PN}:append:scm-npcm845 = " phosphor-ipmi-warm-reset.target"

do_install:append:scm-npcm845() {
        install -D -m 0644 ${WORKDIR}/ipmi-warm-reset.conf \
                        ${D}${systemd_system_unitdir}/phosphor-ipmi-host.service.d/ipmi-warm-reset.conf
}
