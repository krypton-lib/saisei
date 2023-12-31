package saisei.io.format.ebml

import saisei.io.format.ebml.element.ElementDeclaration

public object EBML : ElementDeclaration.MASTER.Actual(EbmlID("EBML", 0x1A, 0x45, 0xDF, 0xA3)) {
    /**
     * The version of EBML specifications used to create the EBML Document. The version of EBML defined in this document
     * is 1, so `EBMLVersion` be 1.
     */
    public val EBMLVersion: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
        EbmlID("EBMLVersion", 0x42, 0x86),
        EBMLIntegerType.Unsigned
    )

    /**
     * The minimum version an EBML reader has to support to read this EBML Document. The `EBMLReadVersion` MUST be less
     * than or equal to `EBMLVersion`
     */
    public val EBMLReadVersion: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
        EbmlID("EBMLReadVersion", 0x42, 0xF7),
        EBMLIntegerType.Unsigned
    )

    /**
     * The maximum permitted length (in octets) of the Element IDs to be found within the EBML Body.
     */
    public val EBMLMaxIDLength: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
        EbmlID("EBMLMaxIDLength", 0x42, 0xF2),
        EBMLIntegerType.Unsigned
    )

    /**
     * The maximum permitted length (in octets) of the expressions of all Element Data Sizes to be found within the EBML
     * Body. The EBMLMaxSizeLength Element documents an upper bound for the length of all Element Data Size expressions
     * within the EBML Body and not an upper bound for the value of all Element Data Size expressions within the EBML Body.
     * EBML Elements that have an Element Data Size expression that is larger in octets than what is expressed by the
     * `EBMLMaxSizeLength` Element are invalid.
     */
    public val EBMLMaxSizeLength: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
        EbmlID("EBMLMaxSizeLength", 0x42, 0xF3),
        EBMLIntegerType.Unsigned
    )

    /**
     * Describes and identifies the content of the EBML Body that follows this EBML Header.
     */
    public val DocType: ElementDeclaration.STRING = ElementDeclaration.STRING.Actual(
        EbmlID("DocType", 0x42, 0x82),
        false
    )

    /**
     * The version of `DocType` interpreter used to create the EBML Document.
     */
    public val DocTypeVersion: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
        EbmlID("DocTypeVersion", 0x42, 0x87),
        EBMLIntegerType.Unsigned
    )

    /**
     * The minimum `DocType` verison an EBML Reader has to support to read this EBML Document. The value of t
     */
    public val DocTypeReadVersion: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
        EbmlID("DocTypeReadVersion", 0x42, 0x85),
        EBMLIntegerType.Unsigned
    )

    public object DocTypeExtension : ElementDeclaration.MASTER.Actual(EbmlID("DocTypeExtension", 0x42, 0x81)) {
        public val DocTypeExtensionName: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
            EbmlID("DocTypeExtensionName", 0x42, 0x83),
            EBMLIntegerType.Unsigned
        )

        public val DocTypeExtensionVersion: ElementDeclaration.INTEGER = ElementDeclaration.INTEGER.Actual(
            EbmlID("DocTypeExtensionVersion", 0x42, 0x84),
            EBMLIntegerType.Unsigned
        )

        init {
            +DocTypeExtensionName
            +DocTypeExtensionVersion
        }
    }

    init {
        +EBMLVersion
        +EBMLReadVersion
        +EBMLMaxIDLength
        +EBMLMaxSizeLength
        +DocType
        +DocTypeVersion
        +DocTypeReadVersion
        +DocTypeExtension
    }
}
